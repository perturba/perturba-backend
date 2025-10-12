package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.common.security.jwt.JwtProvider;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.auth.mapper.OAuthProfile;
import com.hyunwoosing.perturba.domain.auth.mapper.UserOAuthMapper;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public User upsertFromOAuthClaims(Map<String, Object> claims) {
        OAuthProfile p = UserOAuthMapper.toProfile(claims);

        return userRepository.findByEmail(p.email())
                .map(u -> {
                    UserOAuthMapper.applyOAuthUpdate(u, p, LocalDateTime.now());
                    return userRepository.save(u);
                })
                .orElseGet(() -> userRepository.save(UserOAuthMapper.toNewGoogleUser(p)));
    }

    public String issueAccess(User user) {
        return jwtProvider.createAccess(user.getId(), user.getEmail());
    }
}