package com.hyunwoosing.perturba.common.exception;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;

public class BusinessException extends RuntimeException {
    private final ErrorCode code;

    public BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
    public BusinessException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    public ErrorCode code() {
        return code;
    }
}
