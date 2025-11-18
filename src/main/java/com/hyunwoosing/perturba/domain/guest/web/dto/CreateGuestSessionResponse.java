package com.hyunwoosing.perturba.domain.guest.web.dto;

import java.time.Instant;

public record CreateGuestSessionResponse(
        Instant expiresAt,
)
