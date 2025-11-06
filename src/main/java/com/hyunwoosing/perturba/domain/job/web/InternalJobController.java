package com.hyunwoosing.perturba.domain.job.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.domain.job.service.internal.InternalJobService;
import com.hyunwoosing.perturba.domain.job.service.internal.InternalJobStatusService;
import com.hyunwoosing.perturba.domain.job.web.dto.request.internal.CompleteResultRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.internal.JobCompleteRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.internal.JobFailRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.internal.ResultUploadUrlRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.response.internal.ResultUploadUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/internal/jobs")
@RequiredArgsConstructor
public class InternalJobController {

    private final InternalJobStatusService internalJobStatusService;
    private final InternalJobService internalJobService;

    //presigned Put URL 3종 발급
    @PostMapping("/{jobId}/result-upload-urls")
    public ApiResponse<ResultUploadUrlResponse> issueResultUploadUrls(@PathVariable Long jobId) {
        return ApiResponseFactory.success(internalJobService.issueResultUploadUrls(jobId));
    }

    //결과 이미지 저장 완료 (작업완료) -> 결과 Asset 세개 READY로 전환, 이벤트 발행
    @PostMapping("/{jobId}/complete")
    public ApiResponse<Void> complete(@PathVariable Long jobId,
                                      @Valid @RequestBody CompleteResultRequest req) {
        internalJobStatusService.markComplete(jobId, req);
        return ApiResponseFactory.success(null);
    }


    @PostMapping("/{publicId}/progress")
    public ApiResponse<Void> progress(@PathVariable String publicId) {
        internalJobStatusService.markProgress(publicId);
        return ApiResponseFactory.success(null);
    }


    @PostMapping("/{publicId}/fail")
    public ApiResponse<Void> fail(@PathVariable String publicId,
                                  @Valid @RequestBody JobFailRequest request) {
        internalJobStatusService.markFailed(publicId, request);
        return ApiResponseFactory.success(null);
    }
}