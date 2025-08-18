package com.book.dto;

import com.book.common.api.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "도서 검색 응답")
public record BookSearchResponse(
        @Schema(description = "검색 쿼리") String searchQuery,
        @Schema(description = "페이지 정보") PageInfo pageInfo,
        @Schema(description = "검색된 도서 목록") List<BookSummaryResponse> books,
        @Schema(description = "검색 메타데이터") SearchMetadata searchMetadata
) {
    public static <T> BookSearchResponse of(String query,
                                            PageResponse<BookSummaryResponse> pageResponse,
                                            SearchMetadata metadata) {
        return new BookSearchResponse(
                query,
                new PageInfo(
                        pageResponse.currentPage(),
                        pageResponse.pageSize(),
                        pageResponse.totalPages(),
                        pageResponse.totalElements()
                ),
                pageResponse.contents(),
                metadata
        );
    }

    public record PageInfo(
            int currentPage,
            int pageSize,
            int totalPages,
            long totalElements
    ) {}

    public record SearchMetadata(
            long executionTime,
            String strategy
    ) {}
}




