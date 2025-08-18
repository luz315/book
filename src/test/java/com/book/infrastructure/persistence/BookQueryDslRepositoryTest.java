package com.book.infrastructure.persistence;

import com.book.IntegrationTestSupport;
import com.book.common.entity.SearchStrategy;
import com.book.common.exception.CustomException;
import com.book.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookQueryDslRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private BookNativeRepository sut;

    @Autowired
    private JpaBookRepository jpaBookRepository;

    @Test
    void searchBookList_returnsResults() {
        Book book = Book.create("111", "Spring Boot", "Guide", "Author", "Pub", LocalDate.now());
        jpaBookRepository.save(book);

        var result = sut.searchBookList("Spring", 1, 10, SearchStrategy.OR_OPERATION);

        assertThat(result.getContents())
                .extracting(Book::getTitle)
                .contains("Spring Boot");
    }

    @Test
    void searchBookList_invalidKeyword_throwsException() {
        assertThatThrownBy(() -> sut.searchBookList("", 1, 10, SearchStrategy.OR_OPERATION))
                .isInstanceOf(CustomException.class);
    }
}
