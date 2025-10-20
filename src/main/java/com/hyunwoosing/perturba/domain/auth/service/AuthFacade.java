package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;
import com.hyunwoosing.perturba.domain.auth.error.AuthException;
import com.hyunwoosing.perturba.domain.auth.web.dto.MeResponse;
import com.hyunwoosing.perturba.domain.auth.web.dto.TokenResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final AuthProps authProps;

    public MeResponse me(Long userId) {
        if (userId == null) {
            throw new AuthException(AuthErrorCode.UNAUTHENTICATED, "No access token");
        }
        return MeResponse.builder()
                .userId(userId)
                .build();
    }

    public TokenResponse refresh(HttpServletRequest req, HttpServletResponse res) {
        String refreshCookieName = authProps.refresh().cookieName();
        String opaque = findCookie(req, refreshCookieName);
        if (opaque == null) {
            throw new AuthException(AuthErrorCode.REFRESH_NOT_FOUND, "No refresh cookie");
        }

        String rotated = refreshTokenService.rotate(res, opaque, req.getRemoteAddr());
        User owner = refreshTokenService.validateAndGetOwner(rotated);
        String access = authService.issueAccess(owner);

        return TokenResponse.builder()
                .accessToken(access)
                .tokenType("Bearer")
                .expiresIn(authProps.jwt().accessTtlSec())
                .build();
    }

    public void logout(HttpServletResponse res) {
        refreshTokenService.expireCookie(res);
    }

    private String findCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
