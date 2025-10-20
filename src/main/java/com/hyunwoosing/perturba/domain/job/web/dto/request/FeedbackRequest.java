package com.hyunwoosing.perturba.domain.job.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull String strengthEval,   // VERY_STRONG..VERY_WEAK
        @NotNull String distortionEval  // VERY_HIGH..VERY_LOW
) {}