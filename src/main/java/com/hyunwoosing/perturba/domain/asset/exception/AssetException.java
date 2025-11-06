package com.hyunwoosing.perturba.domain.asset.exception;

import com.hyunwoosing.perturba.common.exception.BusinessException;
import com.hyunwoosing.perturba.domain.auth.error.AuthErrorCode;

public class AssetException extends BusinessException {
    public AssetException(AssetErrorCode code, String message) { super(code, message); }
    public AssetException(AssetErrorCode code, String message, Throwable cause) { super(code, message, cause); }
}
