package com.book.dto;

import com.book.domain.Book;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "도서 요약 응답")
public record BookSummaryResponse(
        String isbn,
        String title,
        String subtitle,
        String author,
        LocalDate publishedAt
) {
    public static BookSummaryResponse from(Book book) {
        return new BookSummaryResponse(
                book.getIsbn(),
                book.getTitle(),
                book.getSubtitle(),
                book.getAuthor(),
                book.getPublishedAt()
        );
    }
}
