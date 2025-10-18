package com.hyunwoosing.perturba.domain.asset.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UploadUrlRequest(
        @NotBlank String filename,
        @NotBlank String mimeType,
        @Min(1) long sizeBytes
) {}