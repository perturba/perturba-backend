package com.hyunwoosing.perturba.domain.job.web.dto.request.internal;

import jakarta.validation.constraints.NotBlank;

public record JobFailRequest(
        @NotBlank String reason
) {}