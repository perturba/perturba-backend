package com.hyunwoosing.perturba.common.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean ok,
        T data,
        ApiError error,
        Meta meta
) {}