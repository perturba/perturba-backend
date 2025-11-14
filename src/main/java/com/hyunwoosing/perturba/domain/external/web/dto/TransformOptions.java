package com.hyunwoosing.perturba.domain.external.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransformOptions(
        @NotNull ImageType imageType,
        @Min(0) @Max(10) int intensity
) {
    enum ImageType { GENERAL, PORTRAIT, DOCUMENT }
}

