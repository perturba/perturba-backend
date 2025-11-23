package com.hyunwoosing.perturba.domain.external.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.external.service.ExternalTransformService;
import com.hyunwoosing.perturba.domain.external.web.dto.request.ExternalTransformRequest;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalJobResultResponse;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalTransformResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/external/transform")
@RequiredArgsConstructor
public class ExternalTransformController {

    private final ExternalTransformService externalTransformService;

    @PostMapping
    @Operation(
            summary = "외부 API 변환 작업 생성",
            security = {
                    @SecurityRequirement(name = "external-api-key")
            }
    )
    public ApiResponse<ExternalTransformResponse> createTransform(@AuthenticationPrincipal AuthPrincipal auth,
                                                                  @Valid @RequestBody ExternalTransformRequest req) {
        Long userId  = (auth != null) ? auth.userId()  : null;
        Long apiKeyId = (auth != null) ? auth.apiKeyId() : null;

        return ApiResponseFactory.success(externalTransformService.createTransform(req, userId, apiKeyId));
    }

    @GetMapping("/{jobPublicId}/result")
    @Operation(
            summary = "외부 API 변환 작업 결과 조회",
            security = {
                    @SecurityRequirement(name = "external-api-key")
            }
    )
    public ApiResponse<ExternalJobResultResponse> getResult(@AuthenticationPrincipal AuthPrincipal auth,
                                                            @PathVariable String jobPublicId) {
        Long apiKeyId = (auth != null) ? auth.apiKeyId() : null;

        return ApiResponseFactory.success(externalTransformService.getResult(jobPublicId, apiKeyId));
    }
}
