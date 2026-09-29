package com.example.blog.controller;

import com.example.blog.domain.Article;
import com.example.blog.dto.AddArticleRequest;
import com.example.blog.dto.UpdateArticleRequest;
import com.example.blog.repository.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired BlogRepository blogRepository;

    @BeforeEach
    void setUp() {
        blogRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성: 201 응답과 실제 저장된 값을 검증한다")
    void addArticle() throws Exception {
        // given
        AddArticleRequest request = new AddArticleRequest("제목", "내용");
        // when / then
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("제목"));
        var articles = blogRepository.findAll();
        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getTitle()).isEqualTo("제목");
        assertThat(articles.get(0).getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("전체 조회: 저장한 글 목록을 반환한다")
    void findAllArticles() throws Exception {
        // given
        saveArticle();
        // when / then
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].content").value("내용"));
    }

    @Test
    @DisplayName("단일 조회: 요청한 ID의 글을 반환한다")
    void findArticle() throws Exception {
        // given
        Article article = saveArticle();
        // when / then
        mockMvc.perform(get("/api/articles/{id}", article.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"));
    }

    @Test
    @DisplayName("삭제: 지정한 글만 삭제하고 다른 글은 유지한다")
    void deleteArticle() throws Exception {
        // given
        Article article = saveArticle();
        Article other = blogRepository.save(new Article("다른 글", "남겨둘 내용"));
        // when
        mockMvc.perform(delete("/api/articles/{id}", article.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        // then
        assertThat(blogRepository.existsById(article.getId())).isFalse();
        assertThat(blogRepository.findAll()).extracting(Article::getId)
                .containsExactly(other.getId());
    }

    @Test
    @DisplayName("수정: 트랜잭션 종료 후 DB에 변경 사항이 반영된다")
    void updateArticle() throws Exception {
        // given
        Article saved = saveArticle();
        UpdateArticleRequest request = new UpdateArticleRequest("새 제목", "새 내용");
        // when
        mockMvc.perform(put("/api/articles/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("새 제목"))
                .andExpect(jsonPath("$.content").value("새 내용"));
        // then: 테스트 자체에 @Transactional을 붙이지 않아 커밋 후 재조회한다.
        Article updated = blogRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("새 제목");
        assertThat(updated.getContent()).isEqualTo("새 내용");
        assertThat(blogRepository.count()).isEqualTo(1);
    }

    private Article saveArticle() {
        return blogRepository.save(Article.builder().title("제목").content("내용").build());
    }
}
