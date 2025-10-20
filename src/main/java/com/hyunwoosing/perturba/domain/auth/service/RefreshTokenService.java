package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.common.util.CookieUtil;
import com.hyunwoosing.perturba.common.util.HashUtil;
import com.hyunwoosing.perturba.domain.auth.entity.RefreshToken;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;
import com.hyunwoosing.perturba.domain.auth.error.AuthException;
import com.hyunwoosing.perturba.domain.auth.repository.RefreshTokenRepository;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthProps authProps;

    public String issue(HttpServletResponse res, User user, String clientIp) {
        String opaque = randomOpaque();
        String hashHex = HashUtil.sha256Hex(opaque);

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .tokenHashHex(hashHex)
                .expiresAt(Instant.now().plus(Duration.ofDays(authProps.refresh().ttlDays())))
                .clientIp(clientIp)
                .build();
        refreshTokenRepository.save(rt);

        CookieUtil.add(res,
                authProps.refresh().cookieName(),
                opaque,
                authProps.refresh().ttlDays() * 24 * 3600,
                authProps.refresh().cookiePath(),
                authProps.refresh().cookieDomain(),
                true, true, "Lax");
        return opaque;
    }

    public User validateAndGetOwner(String opaque) {
        if (opaque == null || opaque.isBlank())
            throw new AuthException(AuthErrorCode.REFRESH_NOT_FOUND, "No refresh cookie");

        String hashHex = HashUtil.sha256Hex(opaque);
        RefreshToken token = refreshTokenRepository.findByTokenHashHexAndRevokedAtIsNull(hashHex)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHENTICATED, "Refresh token not found"));
        if (token.isExpired(Instant.now()))
            throw new AuthException(AuthErrorCode.UNAUTHENTICATED, "Refresh token expired");
        return token.getUser();
    }

    public String rotate(HttpServletResponse res, String opaque, String clientIp) {
        if (opaque == null || opaque.isBlank())
            throw new AuthException(AuthErrorCode.REFRESH_NOT_FOUND, "No refresh cookie");

        String hashHex = HashUtil.sha256Hex(opaque);
        RefreshToken cur = refreshTokenRepository.findByTokenHashHexAndRevokedAtIsNull(hashHex)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHENTICATED, "Refresh token not found"));

        cur.revoke(Instant.now());
        refreshTokenRepository.save(cur);

        String nextOpaque = randomOpaque();
        String nextHashHex = HashUtil.sha256Hex(nextOpaque);

        RefreshToken next = RefreshToken.builder()
                .user(cur.getUser())
                .tokenHashHex(nextHashHex)
                .expiresAt(Instant.now().plus(Duration.ofDays(authProps.refresh().ttlDays())))
                .rotatedFrom(cur.getId() != null ? String.valueOf(cur.getId()) : null)
                .clientIp(clientIp)
                .build();
        refreshTokenRepository.save(next);

        CookieUtil.add(res,
                authProps.refresh().cookieName(),
                nextOpaque,
                authProps.refresh().ttlDays() * 24 * 3600,
                authProps.refresh().cookiePath(),
                authProps.refresh().cookieDomain(),
                true, true, "Lax");
        return nextOpaque;
    }

    public void expireCookie(HttpServletResponse res) {
        CookieUtil.expire(res,
                authProps.refresh().cookieName(),
                authProps.refresh().cookiePath(),
                authProps.refresh().cookieDomain());
    }

    private String randomOpaque() {
        byte[] buf = new byte[32];
        new SecureRandom().nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
