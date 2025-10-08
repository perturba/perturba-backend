package com.hyunwoosing.perturba.domain.auth.error;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {
    INVALID_STATE(HttpStatus.BAD_REQUEST),
    PKCE_EXPIRED(HttpStatus.BAD_REQUEST),
    OAUTH_EXCHANGE_FAILED(HttpStatus.BAD_GATEWAY),
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED),
    REFRESH_NOT_FOUND(HttpStatus.UNAUTHORIZED);

    private final HttpStatus status;

    AuthErrorCode(HttpStatus status) {
        this.status = status;
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