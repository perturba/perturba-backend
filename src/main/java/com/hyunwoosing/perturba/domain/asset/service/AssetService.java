package com.hyunwoosing.perturba.domain.asset.service;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                                  String sha256Hex) {

        Asset asset = Asset.builder()
                .job(null)                 //Job 없이 선등록
                .owner(null)
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
