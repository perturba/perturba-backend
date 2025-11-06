package com.hyunwoosing.perturba.domain.job.web.dto.request.internal;

import jakarta.validation.constraints.NotBlank;

public record ResultUploadUrlRequest(
        @NotBlank String perturbedMimeType,
        @NotBlank String deepfakeMimeType,
        @NotBlank String perturbationVisMimeType   // e.g. "image/png"
) {}