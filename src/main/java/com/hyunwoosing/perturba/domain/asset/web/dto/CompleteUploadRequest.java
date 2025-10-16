package com.hyunwoosing.perturba.domain.asset.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CompleteUploadRequest(
        @NotBlank String objectKey,
        @NotBlank String sha256Hex,
        @NotNull Integer width,
        @NotNull Integer height,
        @NotBlank String mimeType,
        @NotNull Long sizeBytes
) {}