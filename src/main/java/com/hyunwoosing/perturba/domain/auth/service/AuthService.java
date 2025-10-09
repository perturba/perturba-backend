package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.common.security.jwt.JwtProvider;
import com.hyunwoosing.perturba.domain.auth.client.google.GoogleOAuthClient;
import com.hyunwoosing.perturba.domain.auth.client.google.dto.GoogleTokenResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.entity.enums.AuthProvider;
import com.hyunwoosing.perturba.domain.user.entity.enums.UserRole;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public User upsertFromGoogleClaims(Map<String, Object> claims) {
        String email = claims.get("email") != null ? claims.get("email").toString() : null;
        String name = claims.get("name") != null ? claims.get("name").toString() : email; //name 없으면 email로 대체
        String picture = claims.get("picture") != null ? claims.get("picture").toString() : null;

        return userRepository.findByEmail(email)
                .map(u -> {
                    u.changeProfile(name, picture);
                    u.markLogin(LocalDateTime.now());
                    return userRepository.save(u);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .avatarUrl(picture)
                                .authProvider(AuthProvider.GOOGLE)
                                .role(UserRole.USER)
                                .isActive(true)
                                .build()
                ));
    }

    public String issueAccess(User user) {
        return jwtProvider.createAccess(user.getId(), user.getEmail());
    }

    private String val(Object o) {
        return o != null ? o.toString() : null;
    }

}