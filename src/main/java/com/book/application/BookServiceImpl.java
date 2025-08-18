package com.book.application;

import com.book.application.port.in.BookService;
import com.book.application.port.out.BookTrendCache;
import com.book.common.api.PageResponse;
import com.book.common.entity.SearchStrategy;
import com.book.common.exception.CustomException;
import com.book.common.exception.custom.BookErrorCode;
import com.book.domain.Book;
import com.book.domain.repository.BookRepository;
import com.book.common.pagination.Pagination;
import com.book.dto.BookDetailResponse;
import com.book.dto.BookSearchResponse;
import com.book.dto.BookSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookTrendCache bookTrendCache;

    @Override
    @Transactional(readOnly = true)
    public BookSearchResponse searchBookList(String keyword, int page, int size) {

        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(BookErrorCode.INVALID_SEARCH_QUERY);
        }

        // 인기 검색어 기록
        try {
            bookTrendCache.recordSearch(keyword);
        } catch (Exception e) {
            log.warn("검색 트렌드 기록 실패 (무시됨): {}", e.getMessage());
        }

        long start = System.nanoTime();

        // 검색 전략 결정
        SearchStrategy strategy = SearchStrategy.fromQuery(keyword);

        // DB 검색 결과 → Pagination
        Pagination<Book> pagination = bookRepository.searchBookList(keyword, page, size, strategy);

        Pagination<BookSummaryResponse> mappedData = pagination.map(BookSummaryResponse::from);

        PageResponse<BookSummaryResponse> pageResponse = PageResponse.from(mappedData);

        long executionTime = (System.nanoTime() - start) / 1_000_000;

        return BookSearchResponse.of(
                keyword,
                pageResponse,
                new BookSearchResponse.SearchMetadata(executionTime, strategy.name())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new CustomException(BookErrorCode.BOOK_NOT_FOUND));
        return BookDetailResponse.from(book);
    }
}