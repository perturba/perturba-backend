package com.hyunwoosing.perturba.domain.external.web.dto;

public record TransformOutputBytes(
        String mime,
        String suggestedFilename,
        byte[] bytes
) {}
