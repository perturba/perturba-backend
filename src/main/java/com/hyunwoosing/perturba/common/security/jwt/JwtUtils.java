package com.hyunwoosing.perturba.common.security.jwt;

import com.hyunwoosing.perturba.common.constants.JwtConstants;
import com.hyunwoosing.perturba.common.security.jwt.dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey key;
    private final Long accessTokenExpiration;

    public JwtUtils( @Value("${jwt.secret}") String secretKey,
                     @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpirationMs;
    }

    public TokenInfo generateTokenInfo(Authentication authentication) {
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(); // Refresh Token은 UUID로 생성
        return TokenInfo.of(accessToken, refreshToken);
    }

    public String generateAccessToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        Date accessTokenExpiresAt = new Date(now + accessTokenExpiration);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(JwtConstants.CLAIM_AUTH, JwtConstants.DEFAULT_ROLE)
                .issuedAt(new Date(now))
                .expiration(accessTokenExpiresAt)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken() { // 만료 시간 등의 정보는 DB에서 관리
        return UUID.randomUUID().toString();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get(JwtConstants.CLAIM_AUTH) == null) {
            throw new JwtException("Token has no authorities.");
        }
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(claims.get(JwtConstants.CLAIM_AUTH).toString())
        );
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
