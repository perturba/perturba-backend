package com.hyunwoosing.perturba.common.web.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private static final Collection<GrantedAuthority> GOOGLE_DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("ROLE_USER"));

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User raw = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
        Map<String, Object> attributes = new LinkedHashMap<>(raw.getAttributes());

        String sub = str(attributes.get("sub"));
        String email = str(attributes.get("email"));
        String name = attributes.get("name") != null ? attributes.get("name").toString() : email;
        String picture = str(attributes.get("picture"));

        Map<String, Object> normalized = Map.of(
                "provider", registrationId,
                "sub", sub,
                "email", email,
                "name", name,
                "picture", picture
        );
        return new DefaultOAuth2User(GOOGLE_DEFAULT_AUTHORITIES, normalized, "email");
    }

    private String str(Object v) { return v != null ? v.toString() : null; }
}
