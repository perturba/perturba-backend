package com.hyunwoosing.perturba.domain.job.service;

import com.hyunwoosing.perturba.domain.job.event.JobCompletedEvent;
import com.hyunwoosing.perturba.domain.job.event.JobFailedEvent;
import com.hyunwoosing.perturba.domain.job.event.JobProgressEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class JobEventEmitter {

    private final JobEventService jobEventService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProgress(JobProgressEvent e) {
        jobEventService.emitProgress(e.publicId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompleted(JobCompletedEvent e) {
        jobEventService.emitCompleted(e.publicId(), e.payload());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFailed(JobFailedEvent e) {
        jobEventService.emitFailed(e.publicId(), e.reason());
    }
}