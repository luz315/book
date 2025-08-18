package com.book.presentation;

import com.book.IntegrationTestSupport;
import com.book.application.port.in.BookTrendService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class BookTrendControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookTrendService bookTrendService;

    @Test
    @DisplayName("인기 검색어 TOP10 조회 성공")
    void trendingKeywords_success() throws Exception {
        // given
        given(bookTrendService.getBookTrendTop10())
                .willReturn(List.of("Spring", "Java"));

        // when & then
        mockMvc.perform(get("/api/v1/books/trending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").value("Spring"))
                .andExpect(jsonPath("$.data[1]").value("Java"));

    }
}
