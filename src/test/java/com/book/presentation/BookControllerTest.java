package com.book.presentation;

import com.book.IntegrationTestSupport;
import com.book.domain.Book;
import com.book.domain.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("도서 검색 시 목록/페이지 정보 반환 + Redis에 검색어 기록")
    void searchBooks() throws Exception {
        // given - 영어 타이틀/저자(FTS simple dict에서 매칭 잘 됨)
        bookRepository.save(Book.create("1111", "Spring Boot", "Core Principles", "Kim", "PubA", LocalDate.now()));
        bookRepository.save(Book.create("2222", "JPA Mastery", null, "Park", "PubB", LocalDate.now()));
        bookRepository.save(Book.create("3333", "Spring Data JPA", null, "Lee", "PubA", LocalDate.now()));

        // when & then
        mockMvc.perform(get("/api/v1/books")
                        .param("keyword", "spring")
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.searchQuery").value("spring"))
                .andExpect(jsonPath("$.data.books.length()").value(2))         // "Spring Boot", "Spring Data JPA"
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value(2));

        // Redis 기록 키: 월별 동적 키 사용
        String key = "trending_keywords:" + YearMonth.now();
        var top1 = redisTemplate.opsForZSet().reverseRange(key, 0, 0);
        assertThat(top1).contains("spring");
    }

    @Test
    @DisplayName("PostgreSQL Full-Text Search NOT 연산(-) 동작")
    void searchBooksWithNotOperation() throws Exception {
        // given
        bookRepository.save(Book.create("1111", "Spring Boot", null, "Kim", "PubA", LocalDate.now()));
        bookRepository.save(Book.create("2222", "JPA Mastery", null, "Park", "PubB", LocalDate.now()));
        bookRepository.save(Book.create("3333", "Spring Data JPA", null, "Lee", "PubA", LocalDate.now()));

        // when & then : "spring-java" → spring 포함 & java 제외
        mockMvc.perform(get("/api/v1/books").param("keyword", "spring-jpa"))        .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.books.length()").value(1))
                .andExpect(jsonPath("$.data.books[0].title").value("Spring Boot"));
    }

    @Test
    @DisplayName("도서 ISBN으로 상세 조회")
    void getBookByIsbn() throws Exception {
        // given
        bookRepository.save(Book.create("1111", "Spring Boot", "Core", "Kim", "PubA", LocalDate.now()));

        // when & then
        mockMvc.perform(get("/api/v1/books/{isbn}", "1111"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isbn").value("1111"))
                .andExpect(jsonPath("$.data.title").value("Spring Boot"))
                .andExpect(jsonPath("$.data.author").value("Kim"));
    }

    @Test
    @DisplayName("존재하지 않는 도서 ISBN 조회 시 404 JSON 에러")
    void getBookByIsbn_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/books/{isbn}", "9999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("도서를 찾을 수 없습니다."));
    }
}