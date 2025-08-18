package com.book.common.exception.custom;

import com.book.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    VALIDATION_ERROR(9000, "입력값 검증 실패", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(9999, "서버 내부 오류", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
