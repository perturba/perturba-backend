package com.hyunwoosing.perturba.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.jwt.JwtUtils;
import com.hyunwoosing.perturba.common.security.jwt.dto.TokenInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
        log.info("OAuth2 Login successful!");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        TokenInfo tokenInfo = jwtUtils.generateTokenInfo(authentication);
        log.debug("Generated Access Token: {}", tokenInfo.accessToken());

        // todo: refresh Token DB에 저장
        // todo: refresh Token 쿠키에 담음

        sendAccessTokenResponse(response, tokenInfo.accessToken());
    }

    private void sendAccessTokenResponse(HttpServletResponse response, String accessToken) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Map<String, Object>> apiResponse = ApiResponseFactory.success(Map.of("access_token", accessToken, "token_type", "Bearer"));

        String responseBody = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(responseBody);
    }
}
