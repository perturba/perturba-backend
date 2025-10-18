package com.hyunwoosing.perturba.domain.asset.web.dto;


import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record UploadUrlResponse(
        String method,
        String uploadUrl,
        Map<String, List<String>> headers,
        String objectKey,
        int expiresInSec
) {}