package com.hyunwoosing.perturba.domain.apikey.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.apikey.service.ApiKeyService;
import com.hyunwoosing.perturba.domain.apikey.web.dto.request.IssueApiKeyRequest;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.ApiKeyMetaResponse;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.IssueApiKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/apikeys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ApiResponse<IssueApiKeyResponse> issueOrRotate(@AuthenticationPrincipal AuthPrincipal principal,
                                                          @RequestBody IssueApiKeyRequest request) {
        Long userId = principal.userId();
        return ApiResponseFactory.success(apiKeyService.issueOrRotate(userId, request));
    }

    @GetMapping
    public ApiResponse<ApiKeyMetaResponse> getKeyMeta(@AuthenticationPrincipal AuthPrincipal principal) {
        Long userId = principal.userId();
        return ApiResponseFactory.success(apiKeyService.getMyKeyMeta(userId));
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> revoke(@AuthenticationPrincipal AuthPrincipal principal,
                                    @PathVariable Long id) {
        Long userId = principal.userId();
        apiKeyService.revokeMyKey(userId);
        return ApiResponseFactory.success(null);
    }
}
