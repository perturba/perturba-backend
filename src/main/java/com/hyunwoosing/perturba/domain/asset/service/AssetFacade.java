package com.hyunwoosing.perturba.domain.asset.service;

import com.hyunwoosing.perturba.common.security.ActorResolver;
import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.web.dto.CompleteUploadRequest;

import com.hyunwoosing.perturba.domain.asset.web.dto.CompleteUploadResponse;
import com.hyunwoosing.perturba.domain.asset.web.dto.UploadUrlRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.UploadUrlResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import com.mongodb.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public CompleteUploadResponse completeUpload(CompleteUploadRequest req, @Nullable Long userId) {
        User owner = null;
        if (userId != null) {
            owner = userRepository.findById(userId).orElse(null);
        }

        String publicUrl = s3PresignService.publicUrl(req.objectKey());

        Asset asset = assetService.createInputAsset(
                publicUrl,
                req.mimeType(),
                req.sizeBytes(),
                req.width(),
                req.height(),
                req.sha256Hex(),
                owner
        );

        return CompleteUploadResponse.builder()
                .assetId(asset.getId())
                .kind(asset.getKind())
                .url(asset.getS3Url())
                .mimeType(asset.getMimeType())
                .sizeBytes(asset.getSizeBytes())
                .width(asset.getWidth())
                .height(asset.getHeight())
                .sha256Hex(asset.getSha256Hex())
                .build();
    }
}
