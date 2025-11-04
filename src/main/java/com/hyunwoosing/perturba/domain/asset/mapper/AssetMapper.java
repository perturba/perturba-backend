package com.hyunwoosing.perturba.domain.asset.mapper;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.CompleteUploadResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AssetMapper {

    public static CompleteUploadResponse toCompleteUploadResponse(Asset asset){
        if (asset == null)
            return CompleteUploadResponse.builder().build();
        return CompleteUploadResponse.builder()
                .assetId(asset.getId())
                .kind(asset.getKind())
                .url(asset.getObjectKey())
                .mimeType(asset.getMimeType())
                .sizeBytes(asset.getSizeBytes())
                .width(asset.getWidth())
                .height(asset.getHeight())
                .sha256Hex(asset.getSha256Hex())
                .build();
    }
}
