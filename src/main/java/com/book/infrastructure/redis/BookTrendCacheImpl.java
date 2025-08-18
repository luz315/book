package com.book.infrastructure.redis;

import com.book.application.port.out.BookTrendCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookTrendCacheImpl implements BookTrendCache {

    private final StringRedisTemplate redisTemplate;

    private String monthlyKey() {
        return "trending_keywords:" + YearMonth.now(); // ex) trending_keywords:2025-08
    }

    @Override
    public void recordSearch(String keyword) {
        String key = monthlyKey();
        redisTemplate.opsForZSet().incrementScore(key, keyword, 1);
        redisTemplate.expire(key, Duration.ofDays(40));
    }

    @Override
    public List<String> getTopKeywords(int limit) {
        String key = monthlyKey();
        return Optional.ofNullable(redisTemplate.opsForZSet()
                .reverseRange(key, 0, limit - 1))
                .orElse(Collections.emptySet())
                .stream()
                .toList();
    }
}
