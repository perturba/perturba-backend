package com.hyunwoosing.perturba.domain.auth.service;

import com.hyunwoosing.perturba.domain.auth.client.google.GoogleOAuthClient;
import com.hyunwoosing.perturba.domain.auth.client.google.dto.GoogleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthService {

    private final GoogleOAuthClient googleClient;

    public String buildGoogleAuthUrl(String codeChallenge, String state, String nonce) {
        return googleClient.buildAuthUrl(codeChallenge, state, nonce);
    }

    public GoogleTokenResponse exchangeCode(String authorizationCode, String codeVerifier) {
        return googleClient.exchangeAuthorizationCode(authorizationCode, codeVerifier);
    }

}