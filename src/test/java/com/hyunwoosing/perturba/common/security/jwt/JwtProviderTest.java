package com.hyunwoosing.perturba.common.security.jwt;


import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.testsupport.TestAuthPropsFactory;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    @Test
    void create_and_verify_access_token() {
        AuthProps props = TestAuthPropsFactory.defaultProps();
        JwtProvider provider = new JwtProvider(props);

        String token = provider.createAccess(42L, "user@test.com");
        assertNotNull(token);

        Claims claims = provider.verify(token);
        assertEquals("42", claims.getSubject());
        assertEquals("perturba-test-issuer", claims.getIssuer());
        assertEquals("user@test.com", claims.get("email"));
        assertNotNull(claims.getExpiration());
    }
}