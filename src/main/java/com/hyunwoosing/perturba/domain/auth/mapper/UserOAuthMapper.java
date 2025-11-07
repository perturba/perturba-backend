package com.hyunwoosing.perturba.domain.auth.mapper;

import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.entity.enums.AuthProvider;
import com.hyunwoosing.perturba.domain.user.entity.enums.UserRole;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@UtilityClass
public class UserOAuthMapper {

    public OAuthProfile toProfile(Map<String, Object> attributes) {
        String email = attributes.get("email") != null ? attributes.get("email").toString() : null;
        String name = attributes.get("name") != null ? attributes.get("name").toString() : email;
        String picture = attributes.get("picture") != null ? attributes.get("picture").toString() : null;
        return new OAuthProfile(email, name, picture);
    }

    public User toNewGoogleUser(OAuthProfile p) {
        return User.builder()
                .email(p.email())
                .name(p.name() != null ? p.name() : p.email())
                .avatarUrl(p.picture())
                .authProvider(AuthProvider.GOOGLE)
                .role(UserRole.USER)
                .isActive(true)
                .build();
    }

    public void applyOAuthUpdate(User user, OAuthProfile p, Instant when) {
        if (p.name() != null)
            user.changeProfile(p.name(), p.picture());
        else
            user.changeProfile(user.getName(), p.picture());
        user.markLogin(when);
    }

}
