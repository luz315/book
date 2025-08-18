package com.book.application.port.in;

import com.book.dto.BookDetailResponse;
import com.book.dto.BookSearchResponse;

import java.util.UUID;

public interface BookService {
    BookSearchResponse searchBookList(String keyword, int page, int size);
    BookDetailResponse getBook(String isbn);
}
