package com.hyunwoosing.perturba.domain.asset.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.ActorResolver;
import com.hyunwoosing.perturba.domain.asset.service.AssetFacade;
import com.hyunwoosing.perturba.domain.asset.web.dto.CompleteUploadRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.CompleteUploadResponse;
import com.hyunwoosing.perturba.domain.asset.web.dto.UploadUrlRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.UploadUrlResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetFacade assetFacade;
    private final ActorResolver actorResolver;

    @PostMapping("/upload-url")
    public ApiResponse<UploadUrlResponse> issueUploadUrl(@Valid @RequestBody UploadUrlRequest req,
                                                         HttpServletRequest httpRequest) {
        Long userId = actorResolver.currentUserId(httpRequest);

        UploadUrlResponse res = assetFacade.issueUploadUrl(req, userId);
        return ApiResponseFactory.success(res);
    }

    @PostMapping("/complete")
    public ApiResponse<CompleteUploadResponse> complete(@Valid @RequestBody CompleteUploadRequest req,
                                                        HttpServletRequest httpRequest) {
        Long userId = actorResolver.currentUserId(httpRequest);

        CompleteUploadResponse res = assetFacade.completeUpload(req, userId);
        return ApiResponseFactory.success(res);
    }
}