package com.hyunwoosing.perturba.domain.job.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobFacade jobFacade;


    @PostMapping
    public ApiResponse<CreateJobResponse> create(@Valid @RequestBody CreateJobRequest req,
                                                 @AuthenticationPrincipal AuthPrincipal authPrincipal,
                                                 @RequestHeader(value = "idempotency-key", required = false) String idemKey) {
        Long userId  = authPrincipal != null ? authPrincipal.userId()  : null;
        Long guestId = authPrincipal != null ? authPrincipal.guestId() : null;

        return ApiResponseFactory.success(jobFacade.create(req, userId, guestId, idemKey));
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
                                                                  @AuthenticationPrincipal AuthPrincipal authPrincipal) {
        Long userId  = authPrincipal != null ? authPrincipal.userId()  : null;
        Long guestId = authPrincipal != null ? authPrincipal.guestId() : null;

        FeedbackResponse res = jobFacade.saveFeedback(publicId, req, userId, guestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseFactory.success(res));
    }
}