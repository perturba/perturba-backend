package com.hyunwoosing.perturba.domain.job.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JobFailRequest(
        @NotBlank String reason
) {}