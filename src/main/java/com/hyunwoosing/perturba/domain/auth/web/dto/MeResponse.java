package com.hyunwoosing.perturba.domain.auth.web.dto;

import lombok.Builder;

@Builder
public record MeResponse(Long userId) {
}
