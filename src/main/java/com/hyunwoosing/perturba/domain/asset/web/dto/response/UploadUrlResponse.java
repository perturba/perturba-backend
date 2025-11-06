package com.hyunwoosing.perturba.domain.asset.web.dto.response;


import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record UploadUrlResponse(
        Long assetId,
        String method,
        String uploadUrl,
        Map<String, List<String>> headers,
        String objectKey,
        int expiresInSec
) {}