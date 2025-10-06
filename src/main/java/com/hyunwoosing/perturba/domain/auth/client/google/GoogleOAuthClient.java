package com.hyunwoosing.perturba.domain.auth.client.google;

import com.hyunwoosing.perturba.domain.auth.client.google.dto.GoogleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private final GoogleEndpoints endpoints;

    public String buildAuthUrl(String codeChallenge, String state, String nonce) {
        return endpoints.buildAuthUrl(codeChallenge, state, nonce);
    }

    public GoogleTokenResponse exchangeAuthorizationCode(String authorizationCode, String codeVerifier) {
        RestClient client = RestClient.create();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", authorizationCode);
        form.add("client_id", endpoints.clientId());
        form.add("client_secret", endpoints.clientSecret());
        form.add("redirect_uri", endpoints.redirectUri());
        form.add("grant_type", "authorization_code");
        form.add("code_verifier", codeVerifier);

        return client.post()
                .uri(endpoints.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }
}