package com.hyunwoosing.perturba.domain.job.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.domain.job.service.InternalJobFacade;
import com.hyunwoosing.perturba.domain.job.web.dto.request.JobCompleteRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.JobFailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/internal/jobs")
@RequiredArgsConstructor
public class InternalJobController {

    private final InternalJobFacade internalJobFacade;


    @PostMapping("/{publicId}/progress")
    public ApiResponse<Void> progress(@PathVariable String publicId) {
        internalJobFacade.progress(publicId);
        return ApiResponseFactory.success(null);
    }

    @PostMapping("/{publicId}/complete")
    public ApiResponse<Void> complete(@PathVariable String publicId,
                                      @Valid @RequestBody JobCompleteRequest request) {
        internalJobFacade.complete(publicId, request);
        return ApiResponseFactory.success(null);
    }

    @PostMapping("/{publicId}/fail")
    public ApiResponse<Void> fail(@PathVariable String publicId,
                                  @Valid @RequestBody JobFailRequest request) {
        internalJobFacade.fail(publicId, request);
        return ApiResponseFactory.success(null);
    }
}