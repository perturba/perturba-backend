package com.hyunwoosing.perturba.domain.job.web;

import com.hyunwoosing.perturba.domain.job.service.JobEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobSseController {

    private final JobEventService jobEventService;

    @GetMapping(value = "/{publicId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "작업 상태 SSE 구독"
    )
    public SseEmitter events(@PathVariable String publicId) {
        return jobEventService.subscribe(publicId);
    }
}