package com.hyunwoosing.perturba.domain.external.mapper;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalJobResultResponse;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalTransformResponse;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExternalJobMapper {

    public ExternalTransformResponse toTransformResponse(TransformJob job) {
        return ExternalTransformResponse.builder()
                .jobPublicId(job.getPublicId())
                .build();
    }

    public ExternalJobResultResponse toResultResponse(TransformJob job, S3PresignService s3PresignService) {
        String perturbedUrl = urlOrNull(job.getPerturbedAsset(), s3PresignService);
        String dfUrl        = urlOrNull(job.getDeepfakeOutputAsset(), s3PresignService);
        String visUrl       = urlOrNull(job.getPerturbationVisAsset(), s3PresignService);

        return ExternalJobResultResponse.builder()
                .status(job.getStatus())
                .failReason(job.getFailReason())
                .perturbedImageUrl(perturbedUrl)
                .deepfakeOutputUrl(dfUrl)
                .perturbationVisUrl(visUrl)
                .build();
    }

    private static String urlOrNull(Asset asset, S3PresignService s3PresignService) {
        if (asset == null) return null;
        return s3PresignService.presignGet(asset.getObjectKey());
    }
}