package com.hyunwoosing.perturba.domain.user.error;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    EMAIL_TAKEN(HttpStatus.CONFLICT),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;
    UserErrorCode(HttpStatus s){ this.status = s; }

    @Override
    public String code() {
        return name();
    }
    @Override
    public HttpStatus httpStatus() {
        return status;
    }
}
