package com.hyunwoosing.perturba.common.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Meta(
        Integer page, Integer size, Long totalElements, Integer totalPages,
        Integer rateLimit, Integer rateRemaining, Long rateResetEpochSec,
        String requestId
) {
    public static Meta ofPage(int page, int size, long total, int totalPages) {
        return new Meta(page, size, total, totalPages, null, null, null, null);
    }
    public static Meta ofRequestId(String requestId) {
        return new Meta(null, null, null, null, null, null, null, requestId);
    }
}