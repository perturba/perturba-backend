package com.hyunwoosing.perturba.domain.job.service;

import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.mapper.JobListMapper;
import com.hyunwoosing.perturba.domain.job.mapper.JobMapper;
import com.hyunwoosing.perturba.domain.job.web.dto.request.CreateJobRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.FeedbackRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.response.*;
import com.hyunwoosing.perturba.domain.user.entity.User;
import org.springframework.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobFacade {

    private final JobService jobService;


    public CreateJobResponse create(CreateJobRequest req, Long userId, Long guestId, @Nullable String idemKey) {
        TransformJob job = jobService.create(req, userId, guestId, idemKey);
        return JobMapper.toCreateJobResponse(job);
    }

    public JobStatusResponse getStatus(String publicId) {
        TransformJob job = jobService.getByPublicId(publicId);
        return JobMapper.toStatusResponse(job);
    }

    public JobResultResponse getResult(String publicId) {
        TransformJob job = jobService.getByPublicId(publicId);
        return JobMapper.toResultResponse(job);
    }

    public FeedbackResponse saveFeedback(String publicId, FeedbackRequest req, Long userId, Long guestId) {
        jobService.saveFeedback(publicId, req, userId, guestId);
        return FeedbackResponse.builder().accepted(true).build();
    }

    public JobListResponse listMyJobs(Long userId, Long guestId, int page, int size) {
        return JobListMapper.toResponse(jobService.listMyJobs(userId, guestId, page, size));
    }
}
