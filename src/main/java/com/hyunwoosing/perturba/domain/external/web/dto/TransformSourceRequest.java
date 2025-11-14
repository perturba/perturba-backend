package com.hyunwoosing.perturba.domain.external.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record TransformSourceRequest(
        String s3ObjectKey,
        @Pattern(regexp = "^https?://.*", message = "sourceUrl must be http(s)") String sourceUrl,
        @NotNull TransformOptions options
) {}
