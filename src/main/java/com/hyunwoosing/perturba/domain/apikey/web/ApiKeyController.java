package com.hyunwoosing.perturba.domain.apikey.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.apikey.service.ApiKeyService;
import com.hyunwoosing.perturba.domain.apikey.web.dto.request.IssueApiKeyRequest;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.ApiKeyMetaResponse;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.IssueApiKeyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/apikeys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @Operation(
            summary = "외부 API 키 발급/갱신 (사용자용)",
            security = {
                    @SecurityRequirement(name = "access-jwt")
            }
    )
    public ApiResponse<IssueApiKeyResponse> issueOrRotate(@AuthenticationPrincipal AuthPrincipal principal,
                                                          @RequestBody IssueApiKeyRequest request) {
        Long userId = principal.userId();
        return ApiResponseFactory.success(apiKeyService.issueOrRotate(userId, request));
    }

    @GetMapping
    @Operation(
            summary = "내 외부 API 키 메타정보 조회",
            security = {
                    @SecurityRequirement(name = "access-jwt")
            }
    )
    public ApiResponse<ApiKeyMetaResponse> getKeyMeta(@AuthenticationPrincipal AuthPrincipal principal) {
        Long userId = principal.userId();
        return ApiResponseFactory.success(apiKeyService.getMyKeyMeta(userId));
    }

    @DeleteMapping("")
    @Operation(
            summary = "내 외부 API 키 폐기",
            security = {
                    @SecurityRequirement(name = "access-jwt")
            }
    )
    public ApiResponse<Void> revoke(@AuthenticationPrincipal AuthPrincipal principal) {
        apiKeyService.revokeMyKey(principal.userId());
        return ApiResponseFactory.success(null);
    }
}
