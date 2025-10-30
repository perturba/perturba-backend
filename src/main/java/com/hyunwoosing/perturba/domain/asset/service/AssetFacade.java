package com.hyunwoosing.perturba.domain.asset.service;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.mapper.AssetMapper;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.CompleteUploadRequest;

import com.hyunwoosing.perturba.domain.asset.web.dto.response.CompleteUploadResponse;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.UploadUrlRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.UploadUrlResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import org.springframework.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetFacade {

    private final S3PresignService s3PresignService;
    private final AssetService assetService;
    private final UserRepository userRepository;

    public UploadUrlResponse issueUploadUrl(UploadUrlRequest req, Long userId) {
        String key = "users/" + (userId == null ? "guest" : userId) + "/" + UUID.randomUUID() + "-" + req.filename();

        PresignedPutObjectRequest signed = s3PresignService.presignPut(key, req.mimeType());

        Map<String, List<String>> headers = signed.signedHeaders();

        Instant exp = signed.expiration();
        int expiresInSec = (exp != null) ? (int) Math.max(0, Duration.between(Instant.now(), exp).getSeconds()) : 0;

        return UploadUrlResponse.builder()
                .method("PUT")
                .uploadUrl(signed.url().toString())
                .headers(headers)
                .objectKey(key)
                .expiresInSec(expiresInSec)
                .build();
    }

    public CompleteUploadResponse completeUpload(CompleteUploadRequest request, @Nullable Long userId) {
        User owner = null;
        if (userId != null) {
            owner = userRepository.findById(userId).orElse(null);
        }

        String publicUrl = s3PresignService.publicUrl(request.objectKey());

        Asset asset = assetService.createInputAsset(publicUrl, request, owner);

        return AssetMapper.toCompleteUploadResponse(asset);
    }
}
