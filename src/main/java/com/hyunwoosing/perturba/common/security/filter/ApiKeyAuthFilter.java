package com.hyunwoosing.perturba.common.security.filter;

import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import com.hyunwoosing.perturba.domain.apikey.repository.ApiKeyRepository;
import com.hyunwoosing.perturba.domain.apikey.service.ApiKeyCrypto;
import com.hyunwoosing.perturba.domain.apikey.service.ApiUsageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private final ApiKeyRepository apiKeyRepository;
    private final ApiUsageService usageService;

    private final Predicate<String> pathMatcher = p -> p.startsWith("/v1/external/");

    private static final String HEADER = "X-Perturba-External-API-Key";
    private static final String PREFIX = "pk_live_";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws IOException, ServletException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();

        boolean needsKey = pathMatcher.test(path);
        String apiKeyPlain = request.getHeader(HEADER);

        //API키 필요없으면 패스
        if (!needsKey) {
            chain.doFilter(request, response);
            return;
        }

        //헤더 체크
        if (!StringUtils.hasText(apiKeyPlain)) {
            reject(response, HttpStatus.UNAUTHORIZED, "missing_api_key", "Provide API key in header " + HEADER);
            return;
        }
        if (!apiKeyPlain.startsWith(PREFIX)) {
            reject(response, HttpStatus.UNAUTHORIZED, "invalid_format", "API key must start with " + PREFIX);
            return;
        }

        //해시화, DB 조회
        String hashHex = ApiKeyCrypto.sha256Hex(apiKeyPlain);
        Optional<ApiKey> opt = apiKeyRepository.findByKeyHashHexAndStatus(hashHex, ApiKeyStatus.ACTIVE);
        if (opt.isEmpty()) {
            reject(response, HttpStatus.UNAUTHORIZED, "not_found_or_revoked", "API key is invalid, revoked, or inactive.");
            return;
        }
        ApiKey key = opt.get();

        //만료 체크
        Instant now = Instant.now();
        if (key.getExpiresAt() != null && now.isAfter(key.getExpiresAt())) {
            reject(response, HttpStatus.UNAUTHORIZED, "expired", "API key expired.");
            return;
        }

        //레이트리밋/일일쿼터(옵션 필드 없으면 스킵)
        if (key.getRatePerMin() != null && key.getRatePerMin() > 0) {
            if (!usageService.tryConsumePerMinute(key.getId(), key.getRatePerMin())) {
                reject(response, HttpStatus.TOO_MANY_REQUESTS, "rate_limited", "Too many requests per minute.");
                return;
            }
        }
        if (key.getDailyQuota() != null && key.getDailyQuota() > 0) {
            if (!usageService.tryConsumeDaily(key.getId(), key.getDailyQuota())) {
                reject(response, HttpStatus.TOO_MANY_REQUESTS, "daily_quota_exceeded", "Daily quota exceeded.");
                return;
            }
        }

        // 인증 컨텍스트 주입 (owner 기준으로 내부 사용자 권한 맥락 부여)
        Long ownerId = Objects.requireNonNull(key.getOwner()).getId();
        AuthPrincipal principal = new AuthPrincipal(ownerId, null, null, AuthorityUtils.NO_AUTHORITIES);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.authorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }

    private static void reject(HttpServletResponse res, HttpStatus status, String code, String msg) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"" + code + "\",\"message\":\"" + msg + "\"}");
    }
}
