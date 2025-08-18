package com.book.common.pagination;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pagination<T> {
    private final List<T> contents;
    private final long totalElements;
    private final int totalPages;
    private final int pageNumber;
    private final int pageSize;

    public static <T> Pagination<T> of(List<T> contents, int pageNumber, int pageSize, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new Pagination<>(contents, totalElements, totalPages, pageNumber, pageSize);
    }

    public <U> Pagination<U> map(Function<T, U> mapper) {
        List<U> mapped = this.contents.stream().map(mapper).toList();
        return new Pagination<>(mapped, this.totalElements, this.totalPages, this.pageNumber, this.pageSize);
    }
}
