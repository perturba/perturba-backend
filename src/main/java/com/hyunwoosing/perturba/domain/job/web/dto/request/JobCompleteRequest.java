package com.hyunwoosing.perturba.domain.job.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record JobCompleteRequest(
        @NotBlank String perturbedAssetUrl,
        @NotBlank String deepfakeUrl,
        @NotBlank String perturbationVisUrl,
        @Min(0) int ttlSec
) {}