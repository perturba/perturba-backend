package com.hyunwoosing.perturba.domain.external.web.dto.request;

import com.hyunwoosing.perturba.domain.job.entity.enums.Intensity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExternalTransformRequest(
        @NotBlank String inputAssetPublicId,
        @NotNull Intensity intensity
) {}