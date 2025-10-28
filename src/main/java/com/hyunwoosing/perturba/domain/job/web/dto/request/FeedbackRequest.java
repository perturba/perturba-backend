package com.hyunwoosing.perturba.domain.job.web.dto.request;

import com.hyunwoosing.perturba.domain.job.entity.enums.DistortionEval;
import com.hyunwoosing.perturba.domain.job.entity.enums.StrengthEval;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull StrengthEval strengthEval,   // VERY_STRONG..VERY_WEAK
        @NotNull DistortionEval distortionEval  // VERY_HIGH..VERY_LOW
) {}