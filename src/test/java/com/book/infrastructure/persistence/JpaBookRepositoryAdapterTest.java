package com.book.infrastructure.persistence;

import com.book.domain.Book;
import com.book.domain.repository.BookRepository;
import com.book.common.pagination.Pagination;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class JpaBookRepositoryAdapterTest {

    private final JpaBookRepository jpaRepo = mock(JpaBookRepository.class);
    private final BookNativeRepository queryDslRepo = mock(BookNativeRepository.class);

    private final BookRepository sut = new JpaBookRepositoryAdapter(jpaRepo, queryDslRepo);

    @Test
    void findById_delegatesToJpaRepo() {
        UUID id = UUID.randomUUID();
        Book book = Book.create("123", "title", "sub", "author", "publisher", LocalDate.now());
        when(jpaRepo.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> result = sut.findById(id);

        assertThat(result).contains(book);
        verify(jpaRepo).findById(id);
    }

    @Test
    void save_delegatesToJpaRepo() {
        Book book = Book.create("123", "title", "sub", "author", "publisher", LocalDate.now());
        when(jpaRepo.save(book)).thenReturn(book);

        Book result = sut.save(book);

        assertThat(result).isEqualTo(book);
        verify(jpaRepo).save(book);
    }

    @Test
    void count_delegatesToJpaRepo() {
        when(jpaRepo.count()).thenReturn(5L);

        long result = sut.count();

        assertThat(result).isEqualTo(5L);
        verify(jpaRepo).count();
    }

    @Test
    void searchBookList_delegatesToQueryDslRepo() {
        Pagination<Book> pagination = Pagination.of(
                java.util.List.of(), 1, 10, 0
        );
    }
}