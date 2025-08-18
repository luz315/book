package com.book.infrastructure.persistence;

import com.book.domain.Book;
import com.book.domain.repository.BookRepository;
import com.book.common.entity.SearchStrategy;
import com.book.common.pagination.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBookRepositoryAdapter implements BookRepository {

    private final JpaBookRepository jpaBookRepository; 
    private final BookNativeRepository bookNativeRepository;

    @Override
    public Optional<Book> findById(UUID id) {
        return jpaBookRepository.findById(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return jpaBookRepository.findByIsbn(isbn);
    }

    @Override
    public Book save(Book book) {
        return jpaBookRepository.save(book);
    }

    @Override
    public long count() {
        return jpaBookRepository.count();
    }

    @Override
    public Pagination<Book> searchBookList(String keyword, int page, int size, SearchStrategy strategy) {
        return bookNativeRepository.searchBookList(keyword, page, size, strategy);
    }
}
