package com.hyunwoosing.perturba.domain.external.web.dto;

public record TransformResultMeta(
        String outputS3Key,
        String mime,
        long sizeBytes,
        String presignedUrl
) {}