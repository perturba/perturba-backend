package com.hyunwoosing.perturba.domain.auth.web.oauth;

import com.hyunwoosing.perturba.domain.auth.service.AuthService;
import com.hyunwoosing.perturba.domain.auth.service.RefreshTokenService;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Value("${perturba.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> claims = oAuth2User.getAttributes();

        User user = authService.upsertFromOAuthClaims(claims);
        refreshTokenService.issue(response, user, request.getRemoteAddr());

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth/callback")
                .queryParam("status", "success")
                .build().toUriString();

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}