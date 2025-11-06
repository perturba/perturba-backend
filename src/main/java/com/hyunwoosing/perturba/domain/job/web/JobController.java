package com.hyunwoosing.perturba.domain.job.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.job.service.JobService;
import com.hyunwoosing.perturba.domain.job.web.dto.request.*;
import com.hyunwoosing.perturba.domain.job.web.dto.response.*;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobResultResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobStatusResponse;
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

    private final JobService jobService;

    @GetMapping
    public JobListResponse list(@AuthenticationPrincipal AuthPrincipal auth,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size) {
        Long userId  = auth != null ? auth.userId()  : null;
        Long guestId = auth != null ? auth.guestId() : null;
        return jobService.listMyJobs(userId, guestId, page, size);
    }

    @PostMapping
    public ApiResponse<CreateJobResponse> create(@AuthenticationPrincipal AuthPrincipal authPrincipal,
                                                 @Valid @RequestBody CreateJobRequest req,
                                                 @RequestHeader(value = "idempotency-key", required = false) String idemKey) {
        Long userId  = authPrincipal != null ? authPrincipal.userId()  : null;
        Long guestId = authPrincipal != null ? authPrincipal.guestId() : null;

        return ApiResponseFactory.success(jobService.create(req, userId, guestId, idemKey));
    }

    @GetMapping("/{publicId}/status")
    public ApiResponse<JobStatusResponse> status(@PathVariable String publicId) {
        return ApiResponseFactory.success(jobService.getStatus(publicId));
    }

    @GetMapping("/{publicId}/result")
    public ApiResponse<JobResultResponse> result(@PathVariable String publicId) {
        return ApiResponseFactory.success(jobService.getResult(publicId));
    }

    @PostMapping("/{publicId}/feedback")
    public ResponseEntity<ApiResponse<Void>> feedback(@AuthenticationPrincipal AuthPrincipal authPrincipal,
                                                                  @PathVariable String publicId,
                                                                  @Valid @RequestBody FeedbackRequest req) {
        Long userId  = authPrincipal != null ? authPrincipal.userId()  : null;
        Long guestId = authPrincipal != null ? authPrincipal.guestId() : null;
        jobService.saveFeedback(publicId, req, userId, guestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseFactory.success(null));
    }
}