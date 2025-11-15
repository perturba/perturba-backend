package com.hyunwoosing.perturba.domain.external.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.external.service.ExternalTransformService;
import com.hyunwoosing.perturba.domain.external.web.dto.request.ExternalTransformRequest;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalJobResultResponse;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalTransformResponse;
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
    public ApiResponse<ExternalTransformResponse> createTransform(@AuthenticationPrincipal AuthPrincipal auth,
                                                                  @Valid @RequestBody ExternalTransformRequest req) {
        Long userId  = (auth != null) ? auth.userId()  : null;
        Long apiKeyId = (auth != null) ? auth.apiKeyId() : null;

        return ApiResponseFactory.success(externalTransformService.createTransform(req, userId, apiKeyId));
    }

    @GetMapping("/{jobPublicId}/result")
    public ApiResponse<ExternalJobResultResponse> getResult(@AuthenticationPrincipal AuthPrincipal auth,
                                                            @PathVariable String jobPublicId) {
        Long apiKeyId = (auth != null) ? auth.apiKeyId() : null;

        return ApiResponseFactory.success(externalTransformService.getResult(jobPublicId, apiKeyId));
    }
}
