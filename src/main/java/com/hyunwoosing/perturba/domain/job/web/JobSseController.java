package com.hyunwoosing.perturba.domain.job.web;

import com.hyunwoosing.perturba.domain.job.service.JobSseFacade;
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

    private final JobSseFacade jobSseFacade;

    @GetMapping(value = "/{publicId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(@PathVariable String publicId) {
        return jobSseFacade.subscribe(publicId);
    }
}