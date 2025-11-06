package com.hyunwoosing.perturba.domain.asset.service;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.common.util.S3ObjectKeyUtil;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetStatus;
import com.hyunwoosing.perturba.domain.asset.mapper.AssetMapper;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.CompleteUploadRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.UploadUrlRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.CompleteUploadResponse;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.UploadUrlResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import jakarta.annotation.Nullable;
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

    //이미지 업로드 신청 (URL 발급)
    @Transactional
    public UploadUrlResponse issueUploadUrl(UploadUrlRequest request, @Nullable Long userId) {
        String objectKey = S3ObjectKeyUtil.inputKey(request.filename(), userId);
        User owner = (userId != null) ? userRepository.findById(userId).orElse(null) : null;

        //sha256 중복 체크
        if(owner != null && request.sha256Hex() != null && !request.sha256Hex().isBlank()){
            Optional<Asset> duplicate = assetRepository.findBySha256HexAndOwner(request.sha256Hex(),  owner);
            if(duplicate.isPresent()){
                return UploadUrlResponse.builder()
                        .method("SKIP")
                        .uploadUrl(null)
                        .headers(Map.of())
                        .objectKey(duplicate.get().getObjectKey())
                        .expiresInSec(0)
                        .build();
            }
        }
        //Asset 생성
        Asset newAsset = AssetMapper.requestToAsset(request, owner, objectKey);
        assetRepository.findByObjectKey(objectKey).orElseGet(() -> assetRepository.save(newAsset));

        //presign 생성
        PresignedPutObjectRequest signed = s3PresignService.presignPut(objectKey, request.mimeType());
        int expiresInSec = Optional.ofNullable(signed.expiration())
                .map(exp -> (int) Math.max(0, Duration.between(Instant.now(), exp).getSeconds()))
                .orElse(0);

        return UploadUrlResponse.builder()
                .method("PUT")
                .uploadUrl((signed.url().toString()))
                .headers(signed.signedHeaders())
                .objectKey(objectKey)
                .expiresInSec(expiresInSec)
                .build();
    }





    //private
}
