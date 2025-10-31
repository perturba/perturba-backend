package com.hyunwoosing.perturba.domain.job.error;

import com.hyunwoosing.perturba.common.exception.BusinessException;

public class SseException extends BusinessException {
    public SseException(SseErrorCode code, String message) {
        super(code, message);
    }
    public SseException(SseErrorCode code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
