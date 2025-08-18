package com.book.common.exception.custom;

import com.book.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BookErrorCode implements ErrorCode {
    BOOK_NOT_FOUND(1000, "도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_ISBN(1001, "이미 존재하는 ISBN입니다.", HttpStatus.CONFLICT),
    INVALID_SEARCH_QUERY(1002, "유효하지 않은 검색어입니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
