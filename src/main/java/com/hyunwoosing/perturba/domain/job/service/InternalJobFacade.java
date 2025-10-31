package com.hyunwoosing.perturba.domain.job.service;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.job.web.dto.request.JobCompleteRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.request.JobFailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalJobFacade {

    private final JobStatusService statusService;
    private final AssetRepository assetRepository;

    public void progress(String publicId) {
        statusService.markProgress(publicId);
    }

    public void complete(String publicId, JobCompleteRequest req) {
        statusService.complete(publicId, req);
    }

    public void fail(String publicId, JobFailRequest req) {
        statusService.markFailed(publicId, req.reason());
    }
}