package com.hyunwoosing.perturba.domain.external.mapper;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalUploadImageResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExternalAssetMapper {

    public ExternalUploadImageResponse toUploadResponse(Asset asset) {
        return ExternalUploadImageResponse.builder()
                .assetPublicId(asset.getPublicId())
                .build();
    }
}