package com.hyunwoosing.perturba.common.api.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode{
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;
    CommonErrorCode(HttpStatus s){ this.status = s; }

    @Override
    public String code() {
        return name();
    }
    @Override
    public HttpStatus httpStatus() {
        return status;
    }
}