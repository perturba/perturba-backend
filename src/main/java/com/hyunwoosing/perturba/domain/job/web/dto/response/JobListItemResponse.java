package com.hyunwoosing.perturba.domain.job.web.dto.response;

import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.OffsetDateTime;

@Builder
public record JobListItemResponse(Long jobId,
                                  String publicId,
                                  JobStatus status,
                                  String inputUrl,
                                  Integer width,
                                  Integer height,
                                  OffsetDateTime createdAt,
                                  OffsetDateTime completedAt
) {
}
