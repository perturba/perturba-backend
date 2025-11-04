package com.hyunwoosing.perturba.domain.job.mapper;

import com.hyunwoosing.perturba.common.storage.S3PresignService;
import com.hyunwoosing.perturba.common.util.TimeUtil;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.job.entity.JobFeedback;
import com.hyunwoosing.perturba.domain.job.entity.TransformJob;
import com.hyunwoosing.perturba.domain.job.web.dto.request.FeedbackRequest;
import com.hyunwoosing.perturba.domain.job.web.dto.response.CreateJobResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobResultResponse;
import com.hyunwoosing.perturba.domain.job.web.dto.response.JobStatusResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JobMapper {

    public static CreateJobResponse toCreateJobResponse(TransformJob job) {
        if (job == null)
            return CreateJobResponse.builder().build();
        return CreateJobResponse.builder()
                .publicId(job.getPublicId())
                .status(job.getStatus() != null ? job.getStatus() : null)
                .build();
    }

    public static JobStatusResponse toStatusResponse(TransformJob job) {
        if (job == null)
            return JobStatusResponse.builder().build();
        return JobStatusResponse.builder()
                .publicId(job.getPublicId())
                .status(job.getStatus() != null ? job.getStatus() : null)
                .startedAt(TimeUtil.toKst(job.getStartedAt()))
                .completedAt(TimeUtil.toKst(job.getCompletedAt()))
                .failReason(job.getFailReason())
                .build();
    }

        public static JobResultResponse toResultResponse(TransformJob job, S3PresignService presign) {
            if (job == null) {
                return JobResultResponse.builder().build();
            }
            return JobResultResponse.builder()
                    .input(toSection(job.getInputAsset(), presign))
                    .perturbed(toSection(job.getPerturbedAsset(), presign))
                    .deepfakeOutput(toSection(job.getDeepfakeOutputAsset(), presign))
                    .perturbationVis(toSection(job.getPerturbationVisAsset(), presign))
                    .createdAt(TimeUtil.toKst(job.getCreatedAt()))
                    .completedAt(TimeUtil.toKst(job.getCompletedAt()))
                    .build();
        }

        public static JobResultResponse.Section toSection(Asset asset, S3PresignService presign) {
            if (asset == null) return null;
            String objectKey = asset.getObjectKey();
            String downloadUrl = presign.presignGet(objectKey);

            return JobResultResponse.Section.builder()
                    .assetId(asset.getId())
                    .objectKey(objectKey)
                    .url(downloadUrl)
                    .mimeType(asset.getMimeType())
                    .width(asset.getWidth())
                    .height(asset.getHeight())
                    .build();
        }



    public static JobFeedback toJobFeedback(TransformJob job, FeedbackRequest request, User user, GuestSession guest) {
        if (job == null || request == null)
            return null;
        return JobFeedback.builder()
                .job(job)
                .user(user)
                .guest(guest)
                .distortionEval(request.distortionEval())
                .strengthEval(request.strengthEval())
                .build();
    }

}