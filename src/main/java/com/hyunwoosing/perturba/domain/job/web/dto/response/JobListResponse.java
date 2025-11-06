package com.hyunwoosing.perturba.domain.job.web.dto.response;

import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record JobListResponse(
        List<JobListItemResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
    @Builder
    public record JobListItemResponse(Long jobId,
                                      String publicId,
                                      JobStatus status,
                                      String inputObjectKey,
                                      Integer width,
                                      Integer height,
                                      OffsetDateTime createdAt,
                                      OffsetDateTime completedAt
    ) {
    }
}