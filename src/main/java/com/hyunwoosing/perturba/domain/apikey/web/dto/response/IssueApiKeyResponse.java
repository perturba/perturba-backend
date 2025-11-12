package com.hyunwoosing.perturba.domain.apikey.web.dto.response;

import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record IssueApiKeyResponse(
        String plaintext,
        String label,
        ApiKeyStatus status,
        Instant createdAt,
        Instant lastUsedAt,
        Instant expiresAt,
        Integer ratePerMin,
        Integer dailyQuota
) {}