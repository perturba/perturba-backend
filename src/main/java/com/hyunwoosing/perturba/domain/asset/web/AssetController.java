package com.hyunwoosing.perturba.domain.asset.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.asset.service.AssetService;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.CompleteUploadRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.CompleteUploadResponse;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.UploadUrlRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.UploadUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/upload-url")
    public ApiResponse<UploadUrlResponse> issueUploadUrl(@AuthenticationPrincipal AuthPrincipal auth,
                                                         @Valid @RequestBody UploadUrlRequest req) {
        Long userId = (auth.userId() != null) ? auth.userId() : null;

        UploadUrlResponse res = assetService.issueUploadUrl(req, userId);
        return ApiResponseFactory.success(res);
    }

    @PostMapping("/complete")
    public ApiResponse<CompleteUploadResponse> complete(@AuthenticationPrincipal AuthPrincipal auth,
                                                        @Valid @RequestBody CompleteUploadRequest req) {
        Long userId = (auth.userId() != null) ? auth.userId() : null;
        return ApiResponseFactory.success(assetService.completeUpload(req, userId));
    }
}