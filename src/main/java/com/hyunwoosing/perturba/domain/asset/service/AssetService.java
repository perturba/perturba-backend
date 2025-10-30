package com.hyunwoosing.perturba.domain.asset.service;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.CompleteUploadRequest;
import com.hyunwoosing.perturba.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    @Transactional
    public Asset createInputAsset(String publicUrl, CompleteUploadRequest request, User owner) {

        if (request.sha256Hex() != null && !request.sha256Hex().isBlank()) {
            Optional<Asset> existing = assetRepository.findBySha256HexAndOwner(request.sha256Hex(), owner);
            if (existing.isPresent()) {
                return existing.get();
            }
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
