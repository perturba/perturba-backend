package com.hyunwoosing.perturba.common.web.advice;

import com.hyunwoosing.perturba.common.api.error.CommonErrorCode;
import com.hyunwoosing.perturba.common.api.error.ErrorCode;
import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        var fe = ex.getBindingResult().getFieldError();
        String message = (fe != null) ? fe.getDefaultMessage() : "validation error";
        Map<String,Object> details = (fe != null) ? Map.of("field", fe.getField()) : null;

        return ResponseEntity
                .status(CommonErrorCode.INVALID_ARGUMENT.httpStatus())
                .body(ApiResponseFactory.fail(CommonErrorCode.INVALID_ARGUMENT, message, details));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ErrorCode code = ex.code();
        return ResponseEntity
                .status(code.httpStatus())
                .body(ApiResponseFactory.fail(code, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex, HttpServletRequest req) {
        // TODO: 로깅
        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_ERROR.httpStatus())
                .body(ApiResponseFactory.fail(CommonErrorCode.INTERNAL_ERROR, "Unexpected server error"));
    }
}
