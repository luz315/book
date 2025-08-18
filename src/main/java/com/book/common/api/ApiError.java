package com.book.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "API 에러 정보")
public class ApiError {

    @Schema(description = "에러 메시지", example = "존재하지 않는 도서입니다.")
    private final String message;

    @Schema(description = "HTTP 상태 코드", example = "404")
    private final int status;

    public ApiError(String message, int status) {
        this.message = message;
        this.status = status;
    }

}
