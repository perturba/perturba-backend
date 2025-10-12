package com.hyunwoosing.perturba.domain.auth.web.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2AuthFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception) {
        ApiResponse<?> body = ApiResponseFactory.fail(AuthErrorCode.INVALID_ID_TOKEN, "OAuth2 login failed");
        try {
            response.setStatus(AuthErrorCode.INVALID_ID_TOKEN.httpStatus().value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), body);
        } catch (Exception e) {
            throw new RuntimeException("oauth2 failure response write error", e);
        }
    }
}
