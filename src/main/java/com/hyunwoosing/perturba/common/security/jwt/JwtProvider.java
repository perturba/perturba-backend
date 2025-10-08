package com.hyunwoosing.perturba.common.security.jwt;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final AuthProps authProps;

    public String createAccess(Long userId, String email) {
        SecretKey key = Keys.hmacShaKeyFor(authProps.jwt().hmacSecret().getBytes(StandardCharsets.UTF_8));
        String issuer = authProps.jwt().issuer();
        long ttlSec = authProps.jwt().accessTtlSec();

        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSec)))
                .claim("email", email)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims verify(String token) {
        SecretKey key = Keys.hmacShaKeyFor(authProps.jwt().hmacSecret().getBytes(StandardCharsets.UTF_8));
        String issuer = authProps.jwt().issuer();

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!issuer.equals(claims.getIssuer())) {
            throw new SecurityException("bad-issuer");
        }
        return claims;
    }
}