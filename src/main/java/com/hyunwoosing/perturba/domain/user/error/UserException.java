package com.hyunwoosing.perturba.domain.user.error;

import com.hyunwoosing.perturba.common.exception.BusinessException;

public class UserException extends BusinessException {
    public UserException(UserErrorCode code, String message) {
        super(code, message);
    }
    public UserException(UserErrorCode code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
