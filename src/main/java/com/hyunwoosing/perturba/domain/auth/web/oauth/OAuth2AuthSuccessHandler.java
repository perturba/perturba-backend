package com.hyunwoosing.perturba.domain.auth.web.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.domain.auth.service.AuthService;
import com.hyunwoosing.perturba.domain.auth.service.RefreshTokenService;
import com.hyunwoosing.perturba.domain.auth.web.dto.TokenResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final AuthProps authProps;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> claims = oAuth2User.getAttributes();

        User user = authService.upsertFromOAuthClaims(claims);
        refreshTokenService.issue(response, user, request.getRemoteAddr());

        TokenResponse token = TokenResponse.builder()
                .accessToken(authService.issueAccess(user))
                .tokenType("Bearer")
                .expiresIn(authProps.jwt().accessTtlSec())
                .build();

        ApiResponse<TokenResponse> body = ApiResponseFactory.success(token);
        try {
            response.setStatus(200);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), body);
        } catch (Exception e) {
            throw new RuntimeException("oauth2 success response write error", e);
        }
    }
}
