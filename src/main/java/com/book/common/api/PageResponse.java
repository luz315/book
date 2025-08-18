package com.book.common.api;

import com.book.common.pagination.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "페이징 응답 래퍼")
public record PageResponse<T>(
        @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
        int currentPage,
        @Schema(description = "페이지당 데이터 개수", example = "20")
        int pageSize,
        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,
        @Schema(description = "전체 데이터 개수", example = "100")
        long totalElements,
        @Schema(description = "조회된 데이터 목록")
        List<T> contents
) {
    public static <T> PageResponse<T> from(Pagination<T> pagination) {
        return new PageResponse<>(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                pagination.getTotalPages(),
                pagination.getTotalElements(),
                pagination.getContents()
        );
    }
}

