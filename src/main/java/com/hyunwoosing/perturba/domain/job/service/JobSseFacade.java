package com.hyunwoosing.perturba.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class JobSseFacade {

    private final JobEventService jobEventService;

    //SSE 구독 전용 이벤트 발행은 JobStatusService → 이벤트 리스너에서만 수행
    public SseEmitter subscribe(String publicId) {
        return jobEventService.subscribe(publicId);
    }
}
