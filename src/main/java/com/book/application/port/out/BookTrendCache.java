package com.book.application.port.out;

import java.util.List;

public interface BookTrendCache {
    void recordSearch(String keyword);
    List<String> getTopKeywords(int limit);
}
