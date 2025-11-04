package com.hyunwoosing.perturba.domain.job.mapper;

import com.hyunwoosing.perturba.common.util.TimeUtil;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobListItemResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobListResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

@UtilityClass
public class JobListMapper {

    public static JobListItemResponse toItem(TransformJob job) {
        Asset input = job.getInputAsset();
        return JobListItemResponse.builder()
                .jobId(job.getId())
                .publicId(job.getPublicId())
                .status(job.getStatus())
                .inputObjectKey(input != null ? input.getObjectKey() : null)
                .width(input != null ? input.getWidth() : null)
                .height(input != null ? input.getHeight() : null)
                .createdAt(TimeUtil.toKst(job.getCreatedAt()))
                .completedAt(TimeUtil.toKst(job.getCompletedAt()))
                .build();
    }

    public static JobListResponse toResponse(Page<TransformJob> page) {
        return JobListResponse.builder()
                .items(page.getContent().stream()
                        .map(JobListMapper::toItem)
                        .collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
