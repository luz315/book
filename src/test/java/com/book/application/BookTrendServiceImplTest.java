// src/test/java/com/book/application/BookTrendServiceImplTest.java
package com.book.application;

import com.book.application.port.out.BookTrendCache;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookTrendServiceImplTest {

    private final BookTrendCache cache = mock(BookTrendCache.class);
    private final BookTrendServiceImpl sut = new BookTrendServiceImpl(cache);

    @Test
    void getBookTrendTop10_delegatesToCache() {
        when(cache.getTopKeywords(10)).thenReturn(List.of("java", "spring"));

        var result = sut.getBookTrendTop10();

        assertThat(result).containsExactly("java", "spring");
        verify(cache).getTopKeywords(10);
    }
}
