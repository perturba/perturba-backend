package com.hyunwoosing.perturba.domain.job.service;

import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.mapper.JobMapper;
import com.hyunwoosing.perturba.domain.job.web.dto.request.CreateJobRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.FeedbackRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.response.CreateJobResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.FeedbackResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobResultResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobStatusResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobFacade {

    private final JobService jobService;


    public CreateJobResponse create(CreateJobRequest req, User user, Long guestId) {
        TransformJob job = jobService.create(req, user, guestId);
        return JobMapper.toCreateJobResponse(job);
    }

    public JobStatusResponse getStatus(String publicId) {
        var job = jobService.getByPublicId(publicId);
        return JobMapper.toStatusResponse(job);
    }

    public JobResultResponse getResult(String publicId) {
        var job = jobService.getByPublicId(publicId);
        return JobMapper.toResultResponse(job);
    }

    public FeedbackResponse saveFeedback(String publicId, FeedbackRequest req, User user, Long guestId) {
        jobService.saveFeedback(publicId, req, user, guestId);
        return FeedbackResponse.builder().accepted(true).build();
    }
}
