package com.hyunwoosing.perturba.domain.asset.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CompleteUploadRequest(
        @NotBlank String objectKey,
        @NotBlank @Size(min = 64, max = 64) @Pattern(regexp = "^[0-9a-fA-F]{64}$") String sha256Hex,
        @NotNull Integer width,
        @NotNull Integer height,
        @NotBlank String mimeType,
        @NotNull Long sizeBytes
) {}