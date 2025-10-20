package com.hyunwoosing.perturba.domain.job.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.ActorResolver;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.job.service.JobFacade;
import com.hyunwoosing.perturba.domain.job.web.dto.request.CreateJobRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.FeedbackRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.response.CreateJobResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.FeedbackResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobResultResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobStatusResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobFacade jobFacade;
    private final ActorResolver actorResolver;

    @PostMapping
    public ApiResponse<CreateJobResponse> create(@Valid @RequestBody CreateJobRequest req,
                                                 HttpServletRequest httpRequest) {
        User user = actorResolver.currentUser(httpRequest).orElse(null);
        Long guestId = actorResolver.currentGuest(httpRequest).map(GuestSession::getId).orElse(null);

        return ApiResponseFactory.success(jobFacade.create(req, user, guestId));
    }

    @GetMapping("/{publicId}/status")
    public ApiResponse<JobStatusResponse> status(@PathVariable String publicId) {
        return ApiResponseFactory.success(jobFacade.getStatus(publicId));
    }

    @GetMapping("/{publicId}/result")
    public ApiResponse<JobResultResponse> result(@PathVariable String publicId) {
        return ApiResponseFactory.success(jobFacade.getResult(publicId));
    }

    @PostMapping("/{publicId}/feedback")
    public ResponseEntity<ApiResponse<FeedbackResponse>> feedback(@PathVariable String publicId,
                                                                  @Valid @RequestBody FeedbackRequest req,
                                                                  HttpServletRequest http) {
        User user = actorResolver.currentUser(http).orElse(null);
        Long guestId = actorResolver.currentGuest(http).map(GuestSession::getId).orElse(null);
        FeedbackResponse res = jobFacade.saveFeedback(publicId, req, user, guestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseFactory.success(res));
    }
}