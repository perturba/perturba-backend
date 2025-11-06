package com.hyunwoosing.perturba.domain.asset.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CompleteUploadRequest(
        @NotBlank String objectKey
) {}