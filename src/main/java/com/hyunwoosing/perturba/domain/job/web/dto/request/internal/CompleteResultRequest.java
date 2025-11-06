package com.hyunwoosing.perturba.domain.job.web.dto.request.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CompleteResultRequest(
        @NotBlank String perturbedObjectKey,
        @NotBlank String deepfakeObjectKey,
        @NotBlank String perturbationVisObjectKey
) {}