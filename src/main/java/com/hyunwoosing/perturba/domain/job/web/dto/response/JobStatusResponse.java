package com.hyunwoosing.perturba.domain.job.web.dto.response;

import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record JobStatusResponse(String publicId,
                                JobStatus status,
                                Instant startedAt,
                                Instant completedAt,
                                String failReason) {
}
