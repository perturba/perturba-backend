package com.hyunwoosing.perturba.domain.auth.client.google;


import com.hyunwoosing.perturba.common.config.props.AuthProps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class GoogleEndpoints {

    private final AuthProps authProps;

    public String buildAuthUrl(String codeChallenge, String state, String nonce) {
        return UriComponentsBuilder.fromUriString(authProps.google().authBaseUri())
                .queryParam("client_id", authProps.google().clientId())
                .queryParam("redirect_uri", authProps.google().redirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .queryParam("code_challenge_method", "S256")
                .queryParam("code_challenge", codeChallenge)
                .queryParam("state", state)
                .queryParam("nonce", nonce)
                .build(true)
                .toUriString();
    }

    public String tokenUri() {
        return authProps.google().tokenUri();
    }

    public String clientId() {
        return authProps.google().clientId();
    }

    public String clientSecret() {
        return authProps.google().clientSecret();
    }

    public String redirectUri() {
        return authProps.google().redirectUri();
    }
}