package com.hyunwoosing.perturba.domain.auth.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.resolver.ClientIpResolver;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.auth.service.AuthService;
import com.hyunwoosing.perturba.domain.auth.web.dto.MeResponse;
import com.hyunwoosing.perturba.domain.auth.web.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ApiResponse<MeResponse> me(@AuthenticationPrincipal AuthPrincipal principal) {
        Long userId = (principal != null) ? principal.userId() : null;
        return ApiResponseFactory.success(authService.me(userId));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(
            @CookieValue(name = "${auth.refresh.cookie-name}", required = false) String refreshOpaque,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        String clientIp = ClientIpResolver.resolve(req);
        return ApiResponseFactory.success(authService.refresh(refreshOpaque, clientIp, res));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse res) {
        authService.logout(res);
        return ApiResponseFactory.success(null);
    }
}
