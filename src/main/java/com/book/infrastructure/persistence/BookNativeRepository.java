package com.book.infrastructure.persistence;

import com.book.common.entity.SearchStrategy;
import com.book.common.exception.CustomException;
import com.book.common.exception.custom.BookErrorCode;
import com.book.domain.Book;
import com.book.common.pagination.Pagination;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookNativeRepository {

    private final EntityManager entityManager;

    public Pagination<Book> searchBookList(String keyword, int page, int size, SearchStrategy strategy) {

        String tsQuery = sanitizeQuery(keyword, strategy);

        StringBuilder nativeSelectQuery = new StringBuilder();
        StringBuilder nativeCountQuery = new StringBuilder();

        String fullTextSearchCondition;
        if (strategy == SearchStrategy.OR_OPERATION || strategy == SearchStrategy.NOT_OPERATION) {
            fullTextSearchCondition = "to_tsvector('simple', title || ' ' || author || ' ' || publisher) @@ to_tsquery(:tsQuery)";
        } else {
            fullTextSearchCondition = "to_tsvector('simple', title || ' ' || author || ' ' || publisher) @@ plainto_tsquery(:tsQuery)";
        }

        nativeSelectQuery.append("SELECT * FROM book WHERE ").append(fullTextSearchCondition);
        nativeCountQuery.append("SELECT COUNT(*) FROM book WHERE ").append(fullTextSearchCondition);

        // 컨텐츠 조회
        List<?> raw = entityManager.createNativeQuery(nativeSelectQuery.toString(), Book.class)
                .setParameter("tsQuery", tsQuery)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();

        List<Book> contents = raw.stream().map(Book.class::cast).toList();

        // 카운트 쿼리
        long total = ((Number) entityManager.createNativeQuery(nativeCountQuery.toString())
                .setParameter("tsQuery", tsQuery)
                .getSingleResult()).longValue();

        return Pagination.of(contents, page, size, total);
    }

    private String sanitizeQuery(String keyword, SearchStrategy strategy) {
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(BookErrorCode.INVALID_SEARCH_QUERY);
        }

        // 공백 제거
        keyword = keyword.trim();

        if (strategy == SearchStrategy.OR_OPERATION && keyword.contains("|")) {
            String[] parts = keyword.split("\\|");
            List<String> valid = Arrays.stream(parts)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();

            if (valid.isEmpty() || valid.size() > 2) {
                throw new CustomException(BookErrorCode.INVALID_SEARCH_QUERY);
            }
            return String.join(" | ", valid);

        } else if (strategy == SearchStrategy.NOT_OPERATION && keyword.contains("-")) {
            String[] parts = keyword.split("-");
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new CustomException(BookErrorCode.INVALID_SEARCH_QUERY);
            }
            return parts[0].trim() + " & !" + parts[1].trim();

        } else {
            if (keyword.isBlank()) {
                throw new CustomException(BookErrorCode.INVALID_SEARCH_QUERY);
            }
            return keyword;
        }
    }
}