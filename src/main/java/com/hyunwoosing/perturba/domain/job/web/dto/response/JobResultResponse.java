package com.hyunwoosing.perturba.domain.job.web.dto.response;

import lombok.Builder;

@Builder
public record JobResultResponse(
        Section input,
        Section perturbed,
        Section deepfakeOutput,
        Section perturbationVis
) {
    @Builder
    public record Section(Long assetId, String url, String mimeType, Integer width, Integer height) {}
}