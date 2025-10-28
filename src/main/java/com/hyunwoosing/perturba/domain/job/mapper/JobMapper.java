package com.hyunwoosing.perturba.domain.job.mapper;

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
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .failReason(job.getFailReason())
                .build();
    }

    public static JobResultResponse toResultResponse(TransformJob job) {
        if (job == null)
            return JobResultResponse.builder().build();
        return JobResultResponse.builder()
                .input(toSection(job.getInputAsset()))
                .perturbed(toSection(job.getPerturbedAsset()))
                .deepfakeOutput(toSection(job.getDeepfakeOutputAsset()))
                .perturbationVis(toSection(job.getPerturbationVisAsset()))
                .build();
    }

    public static JobResultResponse.Section toSection(Asset asset) {
        if (asset == null)
            return null;
        return JobResultResponse.Section.builder()
                .assetId(asset.getId())
                .url(asset.getS3Url())
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