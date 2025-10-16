package com.hyunwoosing.perturba.domain.asset.service;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.mongodb.lang.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    @Transactional
    public Asset createInputAsset(String s3Url,
                                  String mimeType,
                                  Long sizeBytes,
                                  Integer width,
                                  Integer height,
                                  String sha256Hex,
                                  @Nullable User owner) {

        if (sha256Hex != null && !sha256Hex.isBlank()) {
            Optional<Asset> existing = assetRepository.findBySha256Hex(sha256Hex);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Asset asset = Asset.builder()
                .job(null)
                .owner(owner)
                .kind(AssetKind.INPUT)
                .s3Url(s3Url)
                .mimeType(mimeType)
                .sizeBytes(sizeBytes)
                .width(width)
                .height(height)
                .sha256Hex(sha256Hex)
                .build();

        return assetRepository.save(asset);
    }
}
