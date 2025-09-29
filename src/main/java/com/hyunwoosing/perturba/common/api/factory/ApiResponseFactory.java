package com.hyunwoosing.perturba.common.api.factory;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import com.hyunwoosing.perturba.common.api.response.ApiError;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.api.response.Meta;

import java.util.Map;

public final class ApiResponseFactory {
    private ApiResponseFactory(){}

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }
    public static <T> ApiResponse<T> success(T data, Meta meta) {
        return new ApiResponse<>(true, data, null, meta);
    }

    public static <T> ApiResponse<T> fail(ErrorCode code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code.code(), message, null), null);
    }
    public static <T> ApiResponse<T> fail(ErrorCode code, String message, Map<String, Object> details) {
        return new ApiResponse<>(false, null, new ApiError(code.code(), message, details), null);
    }
}
