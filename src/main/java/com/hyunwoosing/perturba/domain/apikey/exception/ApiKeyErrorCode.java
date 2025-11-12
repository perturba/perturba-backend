package com.hyunwoosing.perturba.domain.apikey.exception;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ApiKeyErrorCode implements ErrorCode {
    API_KEY_NOT_FOUND(HttpStatus.NOT_FOUND),
    NOT_YOUR(HttpStatus.FORBIDDEN),
    ;

    private final HttpStatus status;

    ApiKeyErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override public String code() {
        return name();
    }
    @Override public HttpStatus httpStatus() {
        return status;
    }
}
