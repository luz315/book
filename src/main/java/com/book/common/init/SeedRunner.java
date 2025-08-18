package com.book.common.init;

import com.book.domain.Book;
import com.book.domain.repository.BookRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SeedRunner {

    private final BookRepository bookRepository;

    @Bean
    ApplicationRunner seedDatabase() {
        return args -> {
            if (bookRepository.count() > 0) return;

            try (Reader reader = new InputStreamReader(
                    getClass().getResourceAsStream("/seed/books_seed.csv"),
                    StandardCharsets.UTF_8);
                 CSVReader csvReader = new CSVReader(reader)) {

                List<String[]> rows = csvReader.readAll();
                rows.stream().skip(1).forEach(parts -> {
                    Book book = Book.create(
                            parts[0],                               // isbn
                            parts[1],                               // title
                            parts[2].isBlank() ? null : parts[2],   // subtitle
                            parts[3],                               // author
                            parts[4],                               // publisher
                            LocalDate.parse(parts[5])               // publicationDate
                    );
                    bookRepository.save(book);
                });
            }
        };
    }
}
