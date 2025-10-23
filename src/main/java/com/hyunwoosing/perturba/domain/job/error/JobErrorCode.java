package com.hyunwoosing.perturba.domain.job.error;

import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum JobErrorCode implements ErrorCode {
    INPUT_ASSET_NOT_FOUND(HttpStatus.NOT_FOUND),
    ASSET_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN),
    JOB_NOT_COMPLETED(HttpStatus.BAD_REQUEST),
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND),
    ;

    private final HttpStatus status;

    JobErrorCode(HttpStatus s) {
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
