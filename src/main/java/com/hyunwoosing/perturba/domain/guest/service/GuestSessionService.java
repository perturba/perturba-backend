package com.hyunwoosing.perturba.domain.guest.service;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;
import com.hyunwoosing.perturba.domain.auth.error.AuthException;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.guest.repository.GuestSessionRepository;
import com.hyunwoosing.perturba.domain.guest.web.dto.CreateGuestSessionResponse;
import com.hyunwoosing.perturba.domain.guest.web.dto.GuestSessionMeResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestSessionService {

    private final GuestSessionRepository guestSessionRepository;
    private final AuthProps authProps;

    @Transactional
    public CreateGuestSessionResponse issueOrRefresh(HttpServletRequest request, HttpServletResponse response) {
        String cookieName = authProps.guest().cookieName();
        String cookieDomain = authProps.guest().cookieDomain();
        String cookiePath = authProps.guest().cookiePath();
        int ttlDays = authProps.guest().ttlDays();

        Instant now = Instant.now();
        long ttlSeconds = Duration.ofDays(ttlDays).getSeconds();
        Instant newExpiresAt = now.plusSeconds(ttlSeconds);


        GuestSession session = findFromCookie(request, cookieName).filter(gs -> !isExpired(gs, now)).orElseGet(() -> createNewSession(newExpiresAt));

        session.setExpiresAt(newExpiresAt);
        guestSessionRepository.save(session);

        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from(cookieName, session.getPublicToken())
                .httpOnly(true)
                .secure(true)
                .path((cookiePath != null && !cookiePath.isBlank()) ? cookiePath : "/")
                .maxAge(ttlSeconds)
                .sameSite("None");

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            cookieBuilder.domain(cookieDomain);
        }

        ResponseCookie cookie = cookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return CreateGuestSessionResponse.builder()
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Transactional(readOnly = true)
    public GuestSessionMeResponse me(Long guestId) {
        GuestSession session = guestSessionRepository.findById(guestId).orElseThrow(() -> new AuthException(AuthErrorCode.NO_GUEST_SESSION, "게스트 정보가 없습니다."));
        return GuestSessionMeResponse.builder()
                .expiresAt(session.getExpiresAt())
                .build();
    }





    //private
    private Optional<GuestSession> findFromCookie(HttpServletRequest request, String cookieName) {
        var cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        String token = null;
        for (var c : cookies) {
            if (cookieName.equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return guestSessionRepository.findByPublicToken(token);
    }

    private boolean isExpired(GuestSession session, Instant now) {
        return session.getExpiresAt() != null && now.isAfter(session.getExpiresAt());
    }

    private GuestSession createNewSession(Instant expiresAt) {
        String token = UUID.randomUUID().toString().replace("-", "");

        String hashHex;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            hashHex = HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }

        return GuestSession.builder()
                .publicToken(token)
                .tokenHashHex(hashHex)
                .expiresAt(expiresAt)
                .build();
    }
}
