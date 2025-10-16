package com.hyunwoosing.perturba.domain.asset.web.dto;


import lombok.Builder;

import java.util.Map;

@Builder
public record UploadUrlResponse(
        String method,
        String uploadUrl,
        Map<String, String> headers,
        String objectKey,
        int expiresInSec
) {}