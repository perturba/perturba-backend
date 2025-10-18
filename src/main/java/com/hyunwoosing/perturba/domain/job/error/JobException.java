package com.hyunwoosing.perturba.domain.job.error;

import com.hyunwoosing.perturba.common.exception.BusinessException;

public class JobException extends BusinessException {
    public JobException(JobErrorCode code, String message) {
        super(code, message);
    }
    public JobException(JobErrorCode code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
