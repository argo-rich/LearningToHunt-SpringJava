package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.model.Article;
import com.learningtohunt.web.server.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @InjectMocks
    private ArticleService  articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Test
    public void getArticleTest() {
        when(articleRepository.findById(1)).thenReturn(
            Optional.of(new Article(1, "Title1", "Subtitle1", "Content1"))
        );

        Article article = articleService.getArticle(1);

        assertEquals(1, article.getArticleId());
        assertEquals("Title1", article.getTitle());
    }
}
