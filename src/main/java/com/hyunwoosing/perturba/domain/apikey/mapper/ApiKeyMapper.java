package com.hyunwoosing.perturba.domain.apikey.mapper;

import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import com.hyunwoosing.perturba.domain.apikey.web.dto.request.IssueApiKeyRequest;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.ApiKeyMetaResponse;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.IssueApiKeyResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiKeyMapper {
    public static IssueApiKeyResponse toIssueApiKeyResponse(ApiKey entity, String plaintext) {
        return IssueApiKeyResponse.builder()
                .plaintext(plaintext) // 최초 1회만 전달
                .label(entity.getLabel())
                .status(entity.getStatus())
                .expiresAt(entity.getExpiresAt())
                .ratePerMin(entity.getRatePerMin())
                .dailyQuota(entity.getDailyQuota())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ApiKeyMetaResponse toMeta(ApiKey entity) {
        return ApiKeyMetaResponse.builder()
                .label(entity.getLabel())
                .status(entity.getStatus())
                .expiresAt(entity.getExpiresAt())
                .ratePerMin(entity.getRatePerMin())
                .dailyQuota(entity.getDailyQuota())
                .createdAt(entity.getCreatedAt())
                .lastUsedAt(entity.getLastUsedAt())
                .build();
    }

    public static ApiKey toEntity(User owner, String keyHashHex, IssueApiKeyRequest request){
        return ApiKey.builder()
                .owner(owner)
                .label(request.label())
                .keyHashHex(keyHashHex)
                .scopesJson(request.scopesJson())
                .ratePerMin(request.ratePerMin())
                .dailyQuota(request.dailyQuota())
                .status(ApiKeyStatus.ACTIVE)
                .build();
    }
}
