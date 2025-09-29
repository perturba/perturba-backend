package com.hyunwoosing.perturba.common.api.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String code();
    HttpStatus httpStatus();
}
