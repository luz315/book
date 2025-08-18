package com.book.infrastructure.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookTrendCacheImpl 단위 테스트")
class BookTrendCacheImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private BookTrendCacheImpl bookTrendCacheImpl;

    @Nested
    @DisplayName("검색 기록 (recordSearch)")
    class RecordSearch {

        @Test
        @DisplayName("성공: 키워드 점수가 증가하고 만료 시간이 설정된다")
        void recordSearch_Success() {
            // given
            given(redisTemplate.opsForZSet()).willReturn(zSetOperations);

            // when
            bookTrendCacheImpl.recordSearch("Spring");

            // then
            then(zSetOperations).should().incrementScore(anyString(), eq("Spring"), eq(1.0));
            then(redisTemplate).should().expire(anyString(), any());
        }
    }

    @Nested
    @DisplayName("인기 키워드 조회 (getTopKeywords)")
    class GetTopKeywords {

        @Test
        @DisplayName("성공: 키워드가 존재하면 상위 키워드 리스트를 반환한다")
        void getTopKeywords_Success() {
            // given
            given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
            given(zSetOperations.reverseRange(anyString(), eq(0L), eq(2L)))
                    .willReturn(Set.of("Spring", "JPA"));

            // when
            List<String> result = bookTrendCacheImpl.getTopKeywords(3);

            // then
            assertThat(result).containsExactlyInAnyOrder("Spring", "JPA");
        }

        @Test
        @DisplayName("성공: 키워드가 없으면 빈 리스트를 반환한다")
        void getTopKeywords_Empty() {
            // given
            given(redisTemplate.opsForZSet()).willReturn(zSetOperations);
            given(zSetOperations.reverseRange(anyString(), anyLong(), anyLong()))
                    .willReturn(null);

            // when
            List<String> result = bookTrendCacheImpl.getTopKeywords(5);

            // then
            assertThat(result).isEmpty();
        }
    }
}
