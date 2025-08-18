package com.book.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class BookTest {

    @Test
    void createBook_success() {
        Book book = Book.create(
                "1234567890",
                "Effective Java",
                "Best Practices",
                "Joshua Bloch",
                "Addison-Wesley",
                LocalDate.of(2018, 1, 1)
        );

        assertThat(book.getIsbn()).isEqualTo("1234567890");
        assertThat(book.getTitle()).isEqualTo("Effective Java");
        assertThat(book.getSubtitle()).isEqualTo("Best Practices");
        assertThat(book.getAuthor()).isEqualTo("Joshua Bloch");
        assertThat(book.getPublisher()).isEqualTo("Addison-Wesley");
        assertThat(book.getPublishedAt()).isEqualTo(LocalDate.of(2018, 1, 1));
    }
}
