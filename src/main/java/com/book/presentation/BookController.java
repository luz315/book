package com.book.presentation;

import com.book.application.port.in.BookService;
import com.book.common.api.ApiResult;
import com.book.dto.BookDetailResponse;
import com.book.dto.BookSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "도서 검색 및 조회 API", description = "도서 조회 및 검색 API")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "도서 검색", description = "키워드와 페이지 정보를 이용해 도서를 검색합니다.")
    public ApiResult<BookSearchResponse> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResult.success(bookService.searchBookList(keyword, page, size));
    }

    @GetMapping("/{isbn}")
    @Operation(summary = "도서 상세 조회", description = "도서 고유 ISBN으로 상세 정보를 조회합니다.")
    public ApiResult<BookDetailResponse> getBook(@PathVariable String isbn) {
        return ApiResult.success(bookService.getBook(isbn));
    }
}
