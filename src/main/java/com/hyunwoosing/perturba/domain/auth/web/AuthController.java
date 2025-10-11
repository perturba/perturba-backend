package com.hyunwoosing.perturba.domain.auth.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.domain.auth.service.AuthFacade;
import com.hyunwoosing.perturba.domain.auth.web.dto.MeResponse;
import com.hyunwoosing.perturba.domain.auth.web.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @GetMapping("/me")
    public ApiResponse<MeResponse> me(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ApiResponseFactory.success(authFacade.me(userId));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(HttpServletRequest req, HttpServletResponse res) {
        return ApiResponseFactory.success(authFacade.refresh(req, res));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse res) {
        authFacade.logout(res);
        return ApiResponseFactory.success(null);
    }
}
