package com.book.common.exception;

import com.book.common.api.ApiResult;
import com.book.common.exception.custom.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResult<Void>> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("[{}] {} - {}", request.getRequestURI(), e.getErrorCode().getStatus(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ApiResult.error(e.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(CommonErrorCode.VALIDATION_ERROR.getMessage());

        log.warn("[{}] 입력값 검증 실패: {}", request.getRequestURI(), errorMessage);
        return ResponseEntity.badRequest()
                .body(ApiResult.error(CommonErrorCode.VALIDATION_ERROR, errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleOtherExceptions(Exception e, HttpServletRequest request) {
        log.error("[{}] 서버 내부 오류: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.status(CommonErrorCode.INTERNAL_ERROR.getStatus())
                .body(ApiResult.error(CommonErrorCode.INTERNAL_ERROR));
    }
}
