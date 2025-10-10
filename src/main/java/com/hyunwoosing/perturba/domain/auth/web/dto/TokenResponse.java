package com.hyunwoosing.perturba.domain.auth.web.dto;

import lombok.Builder;

@Builder
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
