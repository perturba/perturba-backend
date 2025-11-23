package com.hyunwoosing.perturba.domain.auth.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.resolver.ClientIpResolver;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.auth.service.AuthService;
import com.hyunwoosing.perturba.domain.auth.web.dto.MeResponse;
import com.hyunwoosing.perturba.domain.auth.web.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(
            summary = "내 정보 조회",
            security = {
                    @SecurityRequirement(name = "access-jwt")
            }
    )
    public ApiResponse<MeResponse> me(@AuthenticationPrincipal AuthPrincipal principal) {
        Long userId = (principal != null) ? principal.userId() : null;
        return ApiResponseFactory.success(authService.me(userId));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Access 토큰 재발급",
            description = "perturba_refresh 쿠키로 액세스 토큰을 재발급합니다.",
            security = {
                    @SecurityRequirement(name = "refresh-cookie")
            }
    )
    public ApiResponse<TokenResponse> refresh(@CookieValue(name = "${perturba.auth.refresh.cookie-name}", required = false) String refreshOpaque,
                                              HttpServletRequest req,
                                              HttpServletResponse res) {
        String clientIp = ClientIpResolver.resolve(req);
        return ApiResponseFactory.success(authService.refresh(refreshOpaque, clientIp, res));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "perturba_refresh 쿠키를 폐기하고 삭제합니다.",
            security = {
                    @SecurityRequirement(name = "refresh-cookie")
            }
    )
    public ApiResponse<Void> logout(HttpServletResponse res) {
        authService.logout(res);
        return ApiResponseFactory.success(null);
    }
}
