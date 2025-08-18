package com.book.domain;

import com.book.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "book_id")
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private LocalDate publishedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Book(String isbn, String title, String subtitle,
                 String author, String publisher,
                 LocalDate publishedAt) {
        this.isbn = isbn;
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.publisher = publisher;
        this.publishedAt = publishedAt;
    }

    public static Book create(String isbn, String title, String subtitle,
                              String author, String publisher,
                              LocalDate publishedAt) {
        return Book.builder()
                .isbn(isbn)
                .title(title)
                .subtitle(subtitle)
                .author(author)
                .publisher(publisher)
                .publishedAt(publishedAt)
                .build();
    }
}