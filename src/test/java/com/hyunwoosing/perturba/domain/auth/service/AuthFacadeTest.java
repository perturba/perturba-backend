package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;
import com.hyunwoosing.perturba.domain.auth.error.AuthException;
import com.hyunwoosing.perturba.domain.auth.web.dto.MeResponse;
import com.hyunwoosing.perturba.domain.auth.web.dto.TokenResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.testsupport.TestAuthPropsFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthFacadeTest {

    private RefreshTokenService refreshTokenService;
    private AuthService authService;
    private AuthProps props;
    private AuthFacade facade;

    @BeforeEach
    void setUp() {
        refreshTokenService = mock(RefreshTokenService.class);
        authService = mock(AuthService.class);
        props = TestAuthPropsFactory.defaultProps();
        facade = new AuthFacade(refreshTokenService, authService, props);
    }

    @Test
    void me_ok() {
        MeResponse res = facade.me(42L);
        assertEquals(42L, res.userId());
    }

    @Test
    void me_unauthenticated() {
        AuthException ex = assertThrows(AuthException.class, () -> facade.me(null));
        assertEquals(AuthErrorCode.UNAUTHENTICATED, ex.code());
    }

    @Test
    void refresh_ok() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);

        //쿠키 꺼내기용: private 메서드라 여기선 rotate validate 흐름만 목킹
        when(req.getCookies()).thenReturn(new jakarta.servlet.http.Cookie[]{
                new jakarta.servlet.http.Cookie(props.refresh().cookieName(), "opaque123")
        });
        when(req.getRemoteAddr()).thenReturn("127.0.0.1");

        when(refreshTokenService.rotate(eq(res), anyString(), anyString()))
                .thenReturn("opaqueNext");
        User owner = User.builder().id(99L).email("a@b.c").build();
        when(refreshTokenService.validateAndGetOwner("opaqueNext")).thenReturn(owner);
        when(authService.issueAccess(owner)).thenReturn("jwt-token");

        TokenResponse tr = facade.refresh(req, res);
        assertEquals("jwt-token", tr.accessToken());
        assertEquals("Bearer", tr.tokenType());
        assertEquals(props.jwt().accessTtlSec(), tr.expiresIn());
    }

    @Test
    void refresh_missing_cookie() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getCookies()).thenReturn(null);

        AuthException ex = assertThrows(AuthException.class, () -> facade.refresh(req, res));
        assertEquals(AuthErrorCode.REFRESH_NOT_FOUND, ex.code());
    }
}
