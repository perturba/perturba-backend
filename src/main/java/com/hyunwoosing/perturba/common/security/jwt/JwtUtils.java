package com.hyunwoosing.perturba.common.security.jwt;

import com.hyunwoosing.perturba.common.constants.JwtConstants;
import com.hyunwoosing.perturba.common.security.jwt.dto.TokenInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

    private final Key key;
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
}
