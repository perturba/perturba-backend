package com.hyunwoosing.perturba.domain.auth.web.oauth;

import com.hyunwoosing.perturba.domain.auth.mapper.OAuthProfile;
import com.hyunwoosing.perturba.domain.auth.mapper.UserOAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    // 필요시 ROLE 확장 가능
    private static final Set<GrantedAuthority> DEFAULT_AUTHORITIES = Set.of(() -> "ROLE_USER");

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User raw = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuthProfile profile = UserOAuthMapper.toProfile(raw.getAttributes());

        Map<String, Object> normalized = Map.of(
                "provider", provider,
                "email", profile.email(),
                "name", profile.name(),
                "picture", profile.picture()
        );

        return new DefaultOAuth2User(DEFAULT_AUTHORITIES, normalized, "email");
    }
}
