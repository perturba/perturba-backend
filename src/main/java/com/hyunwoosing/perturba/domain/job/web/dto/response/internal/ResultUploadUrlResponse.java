package com.hyunwoosing.perturba.domain.job.web.dto.response.internal;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ResultUploadUrlResponse(
        Item perturbed,
        Item deepfake,
        Item perturbationVis
) {
    @Builder
    public record Item(
            String method,
            String uploadUrl,
            Map<String, List<String>> headers,
            String objectKey
    ) {}
}