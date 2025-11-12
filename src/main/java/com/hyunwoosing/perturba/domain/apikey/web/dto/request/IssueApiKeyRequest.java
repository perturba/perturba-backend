package com.hyunwoosing.perturba.domain.apikey.web.dto.request;

public record IssueApiKeyRequest(
        String label,
        String scopesJson,
        Integer ratePerMin,
        Integer dailyQuota,
        Long ttlHours
) {}