package com.hyunwoosing.perturba.domain.job.web.dto.response;

import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.OffsetDateTime;

@Builder
public record JobStatusResponse(String publicId,
                                JobStatus status,
                                OffsetDateTime startedAt,
                                OffsetDateTime completedAt,
                                String failReason) {
}
