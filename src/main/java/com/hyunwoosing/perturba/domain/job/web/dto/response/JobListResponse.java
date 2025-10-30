package com.hyunwoosing.perturba.domain.job.web.dto.response;

import lombok.Builder;

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
) {}