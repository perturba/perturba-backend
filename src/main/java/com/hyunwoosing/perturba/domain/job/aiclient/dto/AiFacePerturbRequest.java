package com.hyunwoosing.perturba.domain.job.aiclient.dto;

public record AiFacePerturbRequest(Long jobId,
                                   String publicId,
                                   String inputImageUrl,
                                   String intensity,
                                   String prompt) {
}
