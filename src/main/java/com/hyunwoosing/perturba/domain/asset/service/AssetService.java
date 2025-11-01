package com.hyunwoosing.perturba.domain.asset.service;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.asset.mapper.AssetMapper;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.CompleteUploadRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.UploadUrlRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.CompleteUploadResponse;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.UploadUrlResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import com.mongodb.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
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
public class AssetService {

    private final AssetRepository assetRepository;
    private final S3PresignService s3PresignService;
    private final UserRepository userRepository;

    public UploadUrlResponse issueUploadUrl(UploadUrlRequest request, @Nullable Long userId) {
        String key = "users/" + (userId == null ? "guest" : userId) + "/" + UUID.randomUUID() + "-" + request.filename();

        PresignedPutObjectRequest signed = s3PresignService.presignPut(key, request.mimeType());
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

    @Transactional
    public CompleteUploadResponse completeUpload(CompleteUploadRequest request, @Nullable Long userId) {
        User owner = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
        String publicUrl = s3PresignService.publicUrl(request.objectKey());

        Asset asset = createInputAsset(publicUrl, request, owner);
        return AssetMapper.toCompleteUploadResponse(asset);
    }




    //private
    private Asset createInputAsset(String publicUrl, CompleteUploadRequest request, @Nullable User owner) {
        if (request.sha256Hex() != null && !request.sha256Hex().isBlank() && owner != null) {
            Optional<Asset> existing = assetRepository.findBySha256HexAndOwner(request.sha256Hex(), owner);
            if (existing.isPresent()) return existing.get();
        }

        Asset asset = Asset.builder()
                .job(null)
                .owner(owner)
                .kind(AssetKind.INPUT)
                .s3Url(publicUrl)
                .mimeType(request.mimeType())
                .sizeBytes(request.sizeBytes())
                .width(request.width())
                .height(request.height())
                .sha256Hex(request.sha256Hex())
                .build();

        return assetRepository.save(asset);
    }
}
