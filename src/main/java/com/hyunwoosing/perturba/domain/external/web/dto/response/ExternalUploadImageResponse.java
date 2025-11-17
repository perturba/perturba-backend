package com.hyunwoosing.perturba.domain.external.web.dto.response;

import lombok.Builder;

@Builder
public record ExternalUploadImageResponse(
        String assetPublicId
) {}