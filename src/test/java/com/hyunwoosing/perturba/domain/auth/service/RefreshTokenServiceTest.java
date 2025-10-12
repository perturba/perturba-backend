package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.domain.auth.entity.RefreshToken;
import com.hyunwoosing.perturba.domain.auth.error.AuthException;
import com.hyunwoosing.perturba.domain.auth.repository.RefreshTokenRepository;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.testsupport.TestAuthPropsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    private RefreshTokenRepository repo;
    private AuthProps props;
    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        repo = mock(RefreshTokenRepository.class);
        props = TestAuthPropsFactory.defaultProps();
        service = new RefreshTokenService(repo, props);
    }

    @Test
    void issue_sets_cookie_and_persists_token_hash() {
        User user = User.builder().id(1L).email("u@t.com").build();
        MockHttpServletResponse res = new MockHttpServletResponse();

        String opaque = service.issue(res, user, "127.0.0.1");
        assertNotNull(opaque);
        assertTrue(res.getHeaders("Set-Cookie").stream().anyMatch(h -> h.contains(props.refresh().cookieName())));

        //저장된 엔티티 검증
        ArgumentCaptor<RefreshToken> cap = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repo, times(1)).save(cap.capture());
        RefreshToken saved = cap.getValue();
        assertNotNull(saved.getTokenHashHex());
        assertNull(saved.getRevokedAt());
        assertNotNull(saved.getExpiresAt());
    }

    @Test
    void validate_and_get_owner_ok() {
        //준비: opaque -> hash -> repo 응답
        User user = User.builder().id(2L).email("x@y.z").build();
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(repo.findByTokenHashHexAndRevokedAtIsNull(anyString()))
                .thenReturn(Optional.of(rt));

        User found = service.validateAndGetOwner("opaque");
        assertEquals(2L, found.getId());
    }

    @Test
    void validate_and_get_owner_missing_cookie() {
        assertThrows(AuthException.class, () -> service.validateAndGetOwner(null));
    }

    @Test
    void rotate_replaces_cookie_and_revokes_old() {
        RefreshToken cur = RefreshToken.builder()
                .id(10L)
                .user(User.builder().id(3L).email("e@e.e").build())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        when(repo.findByTokenHashHexAndRevokedAtIsNull(anyString()))
                .thenReturn(Optional.of(cur));

        MockHttpServletResponse res = new MockHttpServletResponse();
        String next = service.rotate(res, "opaque", "127.0.0.1");

        assertNotNull(next);
        assertTrue(res.getHeaders("Set-Cookie").stream()
                .anyMatch(h -> h.contains(props.refresh().cookieName())));
        verify(repo, times(2)).save(any(RefreshToken.class));
    }
}
