package com.hyunwoosing.perturba.domain.external.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.external.service.ExternalAssetService;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalUploadImageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/external/assets")
@RequiredArgsConstructor
public class ExternalAssetController {

    private final ExternalAssetService externalAssetService;

    @PostMapping("/upload")
    @Operation(
            summary = "외부 API 이미지 업로드 (프론트 사용 X)",
            security = {
                    @SecurityRequirement(name = "external-api-key")
            }
    )
    public ApiResponse<ExternalUploadImageResponse> upload(@AuthenticationPrincipal AuthPrincipal auth,
                                                           @RequestPart("file") MultipartFile file) {
        Long userId = (auth != null) ? auth.userId() : null;
        return ApiResponseFactory.success(externalAssetService.uploadExternalImage(file, userId));
    }
}