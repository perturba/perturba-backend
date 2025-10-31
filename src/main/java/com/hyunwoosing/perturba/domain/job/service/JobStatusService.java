package com.hyunwoosing.perturba.domain.job.service;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.entity.enums.JobStatus;
import com.hyunwoosing.perturba.domain.job.error.JobErrorCode;
import com.hyunwoosing.perturba.domain.job.error.JobException;
import com.hyunwoosing.perturba.domain.job.event.JobCompletedEvent;
import com.hyunwoosing.perturba.domain.job.event.JobFailedEvent;
import com.hyunwoosing.perturba.domain.job.event.JobProgressEvent;
import com.hyunwoosing.perturba.domain.job.repository.JobRepository;
import com.hyunwoosing.perturba.domain.job.web.dto.request.JobCompleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobStatusService {

    private final JobRepository jobRepository;
    private final ApplicationEventPublisher events;
    private final AssetRepository assetRepository;

    private TransformJob load(String publicId) {
        return jobRepository.findByPublicId(publicId).orElseThrow(() ->
                new JobException(JobErrorCode.JOB_NOT_FOUND, "해당 작업을 찾을 수 없습니다."));
    }

    @Transactional
    public void markProgress(String publicId) {
        TransformJob job = load(publicId);
        if (job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.FAILED) return;

        job.markProgress(Instant.now());

        //DB 커밋 후에 이벤트 발행
        events.publishEvent(new JobProgressEvent(publicId));
    }

    @Transactional
    public void complete(String publicId, JobCompleteRequest req) {
        TransformJob job = load(publicId);
        Asset perturbed       = resolveByUrlOrNull(req.perturbedAssetUrl());
        Asset deepfakeOutput  = resolveByUrlOrNull(req.deepfakeUrl());
        Asset perturbationVis = resolveByUrlOrNull(req.perturbationVisUrl());

        job.markCompleted(Instant.now(), perturbed, deepfakeOutput, perturbationVis);

        Map<String, Object> payload = Map.of(
                "protectedUrl", req.perturbedAssetUrl(),
                "deepfakeUrl", req.deepfakeUrl(),
                "perturbationVisUrl", req.perturbationVisUrl(),
                "ttlSec", req.ttlSec()
        );
        events.publishEvent(new JobCompletedEvent(publicId, payload));
    }

    @Transactional
    public void markFailed(String publicId, String reason) {
        TransformJob job = load(publicId);
        job.markFailed(reason, Instant.now());

        events.publishEvent(new JobFailedEvent(publicId, reason));
    }


    private Asset resolveByUrlOrNull(String url) {
        if (url == null || url.isBlank())
            return null;

        Optional<Asset> found = assetRepository.findByS3Url(url);
        return found.orElse(null);
    }
}
