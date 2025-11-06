package com.hyunwoosing.perturba.domain.asset.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UploadUrlRequest(
        @NotBlank String filename,
        @NotBlank String mimeType,
        @Min(1) long sizeBytes,

        @Size(min=64, max=64)
        @Pattern(regexp = "^[0-9a-fA-F]{64}$")
        String sha256Hex,

        Integer width,
        Integer height
) {}