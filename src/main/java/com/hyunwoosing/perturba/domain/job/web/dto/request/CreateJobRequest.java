package com.hyunwoosing.perturba.domain.job.web.dto.request;

import com.hyunwoosing.perturba.domain.job.entity.enums.ClientChannel;
import com.hyunwoosing.perturba.domain.job.entity.enums.Intensity;
import com.hyunwoosing.perturba.domain.job.entity.enums.NotifyVia;
import com.hyunwoosing.perturba.domain.job.entity.enums.RequestMode;
import jakarta.validation.constraints.NotNull;

public record CreateJobRequest(
        @NotNull Long inputAssetId,
        @NotNull Intensity intensity,
        @NotNull NotifyVia notifyVia,
        @NotNull ClientChannel clientChannel,
        @NotNull RequestMode requestMode) {
}
