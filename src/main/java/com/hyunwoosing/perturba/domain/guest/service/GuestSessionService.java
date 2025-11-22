package com.hyunwoosing.perturba.domain.guest.service;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;
import com.hyunwoosing.perturba.domain.auth.error.AuthException;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.guest.repository.GuestSessionRepository;
import com.hyunwoosing.perturba.domain.guest.web.dto.CreateGuestSessionResponse;
import com.hyunwoosing.perturba.domain.guest.web.dto.GuestSessionMeResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
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

        //기존 쿠키에서 게스트 세션 찾음
        GuestSession session = findFromCookie(request, cookieName).filter(gs -> !isExpired(gs, now))
                .orElseGet(() -> createNewSession(newExpiresAt));

        if(!isExpired(session, now)) {
            session.setExpiresAt(now);
        }
        guestSessionRepository.save(session);

        Cookie cookie = new Cookie(cookieName, session.getPublicToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int) ttlSeconds);
        cookie.setPath(cookiePath != null ? cookiePath : "/");
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            cookie.setDomain(cookieDomain);
        }

        response.addCookie(cookie);

        return CreateGuestSessionResponse.builder()
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Transactional(readOnly = true)
    public GuestSessionMeResponse me(Long guestId){
        GuestSession session = guestSessionRepository.findById(guestId).orElseThrow(() -> new AuthException(AuthErrorCode.NO_GUEST_SESSION, "게스트 정보가 없습니다."));
        return GuestSessionMeResponse.builder()
                .expiresAt(session.getExpiresAt())
                .build();
    }




    //private
    private Optional<GuestSession> findFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return Optional.empty();

        String token = null;
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }
        if (token == null || token.isBlank())
            return Optional.empty();

        return guestSessionRepository.findByPublicToken(token);
    }

    private boolean isExpired(GuestSession session, Instant now) {
        return session.getExpiresAt() != null && now.isAfter(session.getExpiresAt());
    }

    private GuestSession createNewSession(Instant expiresAt) {
        String token = UUID.randomUUID().toString().replace("-", "");
        return GuestSession.builder()
                .publicToken(token)
                .expiresAt(expiresAt)
                .build();
    }
}
