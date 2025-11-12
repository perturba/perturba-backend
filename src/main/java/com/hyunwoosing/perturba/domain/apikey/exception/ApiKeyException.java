package com.hyunwoosing.perturba.domain.apikey.exception;

import com.hyunwoosing.perturba.common.exception.BusinessException;

public class ApiKeyException extends BusinessException {
    public ApiKeyException(ApiKeyErrorCode code, String message) { super(code, message); }
    public ApiKeyException(ApiKeyErrorCode code, String message, Throwable cause) { super(code, message, cause); }
}
