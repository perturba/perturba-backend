package com.hyunwoosing.perturba.domain.external.web.dto.response;

import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;

public record ExternalJobResultResponse(
        JobStatus status,              //PROGRESS / COMPLETED / FAILED
        String failReason,          //FAILED일 때만
        String perturbedImageUrl,   //보호된 이미지
        String deepfakeOutputUrl,   //DF 결과
        String perturbationVisUrl   //가시화 이미지
) {}