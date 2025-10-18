package com.hyunwoosing.perturba.domain.job.web.dto.response;

import java.time.Instant;

public record JobStatusResponse(String publicId,
                                String status,
                                Instant startedAt,
                                Instant completedAt,
                                String failReason) {
}
