package com.hyunwoosing.perturba.domain.job.web.dto.response;

import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import lombok.Builder;

@Builder
public record CreateJobResponse(String publicId, JobStatus status) {
}
