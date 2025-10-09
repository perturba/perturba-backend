package com.hyunwoosing.perturba.domain.auth.error;

import com.hyunwoosing.perturba.common.exception.BusinessException;

public class AuthException extends BusinessException {
    public AuthException(AuthErrorCode code, String message) { super(code, message); }
    public AuthException(AuthErrorCode code, String message, Throwable cause) { super(code, message, cause); }
}