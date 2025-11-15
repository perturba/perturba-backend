package com.hyunwoosing.perturba.domain.external.web.dto.response;

import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import lombok.Builder;

@Builder
public record ExternalTransformResponse(
        String jobPublicId
) {}