package com.book.infrastructure.persistence;

import com.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaBookRepository extends JpaRepository<Book, UUID> {
}
