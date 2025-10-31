package com.hyunwoosing.perturba.domain.job.error;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SseErrorCode implements ErrorCode {
    NOT_ALLOWED_STATUS(HttpStatus.FORBIDDEN),
    ;

    private final HttpStatus status;

    SseErrorCode(HttpStatus s) {
        this.status = s;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public HttpStatus httpStatus() {
        return status;
    }
}
