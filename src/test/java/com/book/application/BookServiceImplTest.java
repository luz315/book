package com.book.application;

import com.book.application.port.out.BookTrendCache;
import com.book.common.entity.SearchStrategy;
import com.book.common.exception.CustomException;
import com.book.common.exception.custom.BookErrorCode;
import com.book.domain.Book;
import com.book.domain.repository.BookRepository;
import com.book.common.pagination.Pagination;
import com.book.dto.BookDetailResponse;
import com.book.dto.BookSearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceImpl 단위 테스트")
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookTrendCache bookTrendCache;

    @Nested
    @DisplayName("도서 검색 (searchBookList)")
    class SearchBookList {

        @Test
        @DisplayName("성공: 유효한 키워드로 검색 시 검색 결과를 반환한다")
        void searchBookList_Success() {
            // given
            String keyword = "JPA";
            int page = 1;
            int size = 10;
            Book testBook = Book.create("1234", "JPA 프로그래밍", null, "김영한", "에이콘", LocalDate.now());
            Pagination<Book> pagination = Pagination.of(List.of(testBook), page, size, 1L);

            given(bookRepository.searchBookList(anyString(), anyInt(), anyInt(), any(SearchStrategy.class)))
                    .willReturn(pagination);
            willDoNothing().given(bookTrendCache).recordSearch(keyword);

            // when
            BookSearchResponse response = bookServiceImpl.searchBookList(keyword, page, size);

            // then
            assertThat(response).isNotNull();
            assertThat(response.searchQuery()).isEqualTo(keyword);
            assertThat(response.books()).hasSize(1);
            assertThat(response.books().get(0).title()).isEqualTo("JPA 프로그래밍");
            then(bookRepository).should().searchBookList(keyword, page, size, SearchStrategy.SIMPLE);
            then(bookTrendCache).should().recordSearch(keyword);
        }

        @Test
        @DisplayName("실패: 검색 키워드가 null이거나 공백이면 CustomException을 던진다")
        void searchBookList_Fail_InvalidKeyword() {
            // given
            String nullKeyword = null;
            String blankKeyword = "  ";

            // when & then
            assertThatThrownBy(() -> bookServiceImpl.searchBookList(nullKeyword, 1, 10))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BookErrorCode.INVALID_SEARCH_QUERY);

            assertThatThrownBy(() -> bookServiceImpl.searchBookList(blankKeyword, 1, 10))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BookErrorCode.INVALID_SEARCH_QUERY);

            then(bookRepository).should(never()).searchBookList(anyString(), anyInt(), anyInt(), any(SearchStrategy.class));
            then(bookTrendCache).should(never()).recordSearch(anyString());
        }

        @Test
        @DisplayName("성공(예외처리): 검색 트렌드 기록 실패 시 경고 로그만 남기고 정상 처리된다")
        void searchBookList_Success_CacheFails() {
            // given
            String keyword = "Spring";
            Pagination<Book> emptyPagination = Pagination.of(List.of(), 1, 10, 0L);
            given(bookRepository.searchBookList(anyString(), anyInt(), anyInt(), any(SearchStrategy.class)))
                    .willReturn(emptyPagination);
            willThrow(new RuntimeException("Redis connection failed")).given(bookTrendCache).recordSearch(keyword);

            // when
            BookSearchResponse response = bookServiceImpl.searchBookList(keyword, 1, 10);

            // then
            assertThat(response).isNotNull();
            then(bookRepository).should().searchBookList(anyString(), anyInt(), anyInt(), any(SearchStrategy.class));
            then(bookTrendCache).should().recordSearch(keyword); // 호출은 되었는지 확인
        }
    }

    @Nested
    @DisplayName("도서 상세 조회 (getBook)")
    class GetBook {

        @Test
        @DisplayName("성공: 존재하는 ID로 조회 시 도서 상세 정보를 반환한다")
        void getBook_Success() {
            // given
            String isbn = "5678";
            Book testBook = Book.create(isbn, "토비의 스프링", null, "이일민", "에이콘", LocalDate.now());
            given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(testBook));

            // when
            BookDetailResponse response = bookServiceImpl.getBook(isbn);

            // then
            assertThat(response).isNotNull();
            assertThat(response.title()).isEqualTo("토비의 스프링");
            assertThat(response.author()).isEqualTo("이일민");
            then(bookRepository).should().findByIsbn(isbn);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 ISBN으로 조회 시 CustomException을 던진다")
        void getBookByIsbn_Fail_NotFound() {
            // given
            String nonExistentIsbn = "9999";
            given(bookRepository.findByIsbn(nonExistentIsbn)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookServiceImpl.getBook(nonExistentIsbn))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BookErrorCode.BOOK_NOT_FOUND);

            then(bookRepository).should().findByIsbn(nonExistentIsbn);
        }
    }
}