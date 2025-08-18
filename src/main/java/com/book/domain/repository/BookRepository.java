package com.book.domain.repository;

import com.book.domain.Book;
import java.util.UUID;
import com.book.common.entity.SearchStrategy;
import com.book.common.pagination.Pagination;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(UUID id);
    Book save(Book book);
    long count();
    Pagination<Book> searchBookList(String keyword, int page, int size, SearchStrategy strategy);
}
