package com.hyunwoosing.perturba.domain.job.web.dto.response;

import lombok.Builder;

@Builder
public record FeedbackResponse(boolean accepted) {
}
