package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.common.security.JwtProvider;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;
import com.hyunwoosing.perturba.domain.auth.error.AuthException;
import com.hyunwoosing.perturba.domain.auth.mapper.OAuthProfile;
import com.hyunwoosing.perturba.domain.auth.mapper.UserOAuthMapper;
import com.hyunwoosing.perturba.domain.auth.web.dto.MeResponse;
import com.hyunwoosing.perturba.domain.auth.web.dto.TokenResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthProps authProps;

    @Transactional
    public User upsertFromOAuthClaims(Map<String, Object> claims) {
        OAuthProfile profile = UserOAuthMapper.toProfile(claims);
        return userRepository.findByEmail(profile.email())
                .map(user -> {
                    UserOAuthMapper.applyOAuthUpdate(user, profile, Instant.now());
                    return userRepository.save(user);
                })
                .orElseGet(() -> userRepository.save(UserOAuthMapper.toNewGoogleUser(profile)));
    }

    public String issueAccess(User user) {
        return jwtProvider.createAccess(user.getId(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public MeResponse me(Long userId) {
        if (userId == null) {
            throw new AuthException(AuthErrorCode.UNAUTHENTICATED, "No access token");
        }
        return MeResponse.builder()
                .userId(userId)
                .build();
    }

    @Transactional
    public TokenResponse refresh(String refreshOpaque, String clientIp, HttpServletResponse res) {
        if (refreshOpaque == null || refreshOpaque.isBlank()) {
            throw new AuthException(AuthErrorCode.REFRESH_NOT_FOUND, "No refresh cookie");
        }

        String rotated = refreshTokenService.rotate(res, refreshOpaque, clientIp);
        User owner = refreshTokenService.validateAndGetOwner(rotated);
        String access = issueAccess(owner);

        return TokenResponse.builder()
                .accessToken(access)
                .tokenType("Bearer")
                .expiresIn(authProps.jwt().accessTtlSec())
                .build();
    }

    public void logout(HttpServletResponse res) {
        refreshTokenService.expireCookie(res);
    }
}
