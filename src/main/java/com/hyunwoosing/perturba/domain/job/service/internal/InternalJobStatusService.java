package com.hyunwoosing.perturba.domain.job.service.internal;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetStatus;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import com.hyunwoosing.perturba.domain.job.error.JobErrorCode;
import com.hyunwoosing.perturba.domain.job.error.JobException;
import com.hyunwoosing.perturba.domain.job.event.JobCompletedEvent;
import com.hyunwoosing.perturba.domain.job.event.JobFailedEvent;
import com.hyunwoosing.perturba.domain.job.event.JobProgressEvent;
import com.hyunwoosing.perturba.domain.job.repository.JobRepository;
import com.hyunwoosing.perturba.domain.job.web.dto.request.internal.CompleteResultRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.internal.JobFailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InternalJobStatusService {

    private final JobRepository jobRepository;
    private final ApplicationEventPublisher events;
    private final AssetRepository assetRepository;

    @Transactional
    public void markProgress(String publicId) {
        TransformJob job = load(publicId);
        if (job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.FAILED) return;
        job.markProgress(Instant.now());
        //DB 커밋 후에 이벤트 발행
        events.publishEvent(new JobProgressEvent(publicId));
    }

    @Transactional
    public void markCompleteById(Long jobId, CompleteResultRequest request){
        TransformJob job = jobRepository.findById(jobId).orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND, "해당 작업을 찾을 수 없습니다."));

        Asset perturbed = assetRepository.findByObjectKey(request.perturbedObjectKey()).orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND, "perturbed asset not found"));
        Asset deepfake = assetRepository.findByObjectKey(request.deepfakeObjectKey()).orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND, "deepfake asset not found"));
        Asset vis = assetRepository.findByObjectKey(request.perturbationVisObjectKey()).orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND, "vis asset not foun"));

        perturbed.setStatus(AssetStatus.READY);
        deepfake.setStatus(AssetStatus.READY);
        vis.setStatus(AssetStatus.READY);

        job.markCompleted(Instant.now(), perturbed, deepfake, vis);
        Map<String, Object> payload = Map.of(
                "perturbedKey", request.perturbedObjectKey(),
                "deepfakeKey", request.deepfakeObjectKey(),
                "perturbationVisKey", request.perturbationVisObjectKey()
        );
        events.publishEvent(new JobCompletedEvent(job.getPublicId(), payload));
    }



    @Transactional
    public void markFailed(String publicId, JobFailRequest request) {
        TransformJob job = load(publicId);
        job.markFailed(request.reason(), Instant.now());

        events.publishEvent(new JobFailedEvent(publicId, request.reason()));
    }


    //private
    private TransformJob load(String publicId) {
        return jobRepository.findByPublicId(publicId).orElseThrow(() ->
                new JobException(JobErrorCode.JOB_NOT_FOUND, "해당 작업을 찾을 수 없습니다."));
    }

    private Asset resolveByUrlOrNull(String objectKey) {
        if (objectKey == null || objectKey.isBlank())
            return null;
        Optional<Asset> found = assetRepository.findByObjectKey(objectKey);
        return found.orElse(null);
    }
}
