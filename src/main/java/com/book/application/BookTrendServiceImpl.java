package com.book.application;

import com.book.application.port.in.BookTrendService;
import com.book.application.port.out.BookTrendCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookTrendServiceImpl implements BookTrendService {

    private final BookTrendCache bookTrendCache;

    @Override
    @Transactional(readOnly = true)
    public List<String> getBookTrendTop10() {
        return bookTrendCache.getTopKeywords(10);
    }
}
