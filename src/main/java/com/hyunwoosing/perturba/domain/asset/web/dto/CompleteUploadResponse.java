package com.hyunwoosing.perturba.domain.asset.web.dto;

import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import lombok.Builder;

@Builder
public record CompleteUploadResponse(
        Long assetId,
        AssetKind kind,
        String url,
        String mimeType,
        Long sizeBytes,
        Integer width,
        Integer height,
        String sha256Hex
) {}