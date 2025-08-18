package com.book.common.api;

import com.book.common.exception.ErrorCode;
import com.book.common.pagination.Pagination;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"success", "data", "error"})
@Schema(description = "API 공통 응답 래퍼")
public class ApiResult<T> {

    @Schema(description = "성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "성공 시 반환할 데이터")
    private final T data;

    @Schema(description = "실패 시 에러 정보")
    private final ApiError error;

    public ApiResult(boolean success, T data, ApiError error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, data, null);
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode) {
        return new ApiResult<>(false, null,
                new ApiError(errorCode.getMessage(), errorCode.getCode()));
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode, String customMessage) {
        return new ApiResult<>(false, null,
                new ApiError(customMessage, errorCode.getCode()));
    }
    public static <T> ApiResult<PageResponse<T>> successPage(Pagination<T> pagination) {
        return new ApiResult<>(true, PageResponse.from(pagination), null);
    }
}
