package com.hyunwoosing.perturba.domain.guest.web.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CreateGuestSessionResponse(
        Instant expiresAt
) {

}
