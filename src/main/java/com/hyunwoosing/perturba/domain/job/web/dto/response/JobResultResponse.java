package com.hyunwoosing.perturba.domain.job.web.dto.response;

public record JobResultResponse(
        Section input,
        Section perturbed,
        Section deepfakeOutput,
        Section perturbationVis
) {
    public record Section(Long assetId, String url, String mimeType, Integer width, Integer height) {}
}