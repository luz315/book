package com.book.presentation;

import com.book.application.port.in.BookTrendService;
import com.book.common.api.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "인기 검색어 조회 API", description = "인기 검색어 관련 API")
public class BookTrendController {

    private final BookTrendService bookTrendService;

    @GetMapping("/trending")
    @Operation(summary = "인기 검색어 TOP N 조회", description = "Redis 기반으로 현재 달의 인기 검색어를 집계하여 TOP N을 반환합니다.")
    public ApiResult<List<String>> trendingKeywords() {
        return ApiResult.success(bookTrendService.getBookTrendTop10());
    }
}

