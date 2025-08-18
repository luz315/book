package com.book.dto;

import com.book.domain.Book;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "도서 상세 응답")
public record BookDetailResponse(
        @Schema(description = "ISBN") String isbn,
        @Schema(description = "제목") String title,
        @Schema(description = "부제목") String subtitle,
        @Schema(description = "저자") String author,
        @Schema(description = "출판사") String publisher,
        @Schema(description = "출간일") LocalDate publishedAt
) {
    public static BookDetailResponse from(Book book) {
        return new BookDetailResponse(
                book.getIsbn(),
                book.getTitle(),
                book.getSubtitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedAt()
        );
    }
}
