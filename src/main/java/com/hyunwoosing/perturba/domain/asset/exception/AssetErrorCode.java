package com.hyunwoosing.perturba.domain.asset.exception;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AssetErrorCode implements ErrorCode {
    UNKNOWN_OBJECT_KEY(HttpStatus.BAD_REQUEST),
    NOT_YOUR_ASSET(HttpStatus.FORBIDDEN),
    ;

    private final HttpStatus status;

    AssetErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override public String code() {
        return name();
    }
    @Override public HttpStatus httpStatus() {
        return status;
    }
}
