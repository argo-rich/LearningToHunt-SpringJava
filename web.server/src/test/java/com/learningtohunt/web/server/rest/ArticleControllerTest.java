package com.learningtohunt.web.server.rest;

import com.learningtohunt.web.server.Application;
import com.learningtohunt.web.server.config.ProjectSecurityConfig;
import com.learningtohunt.web.server.model.Article;
import com.learningtohunt.web.server.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@ContextConfiguration(classes={Application.class, ProjectSecurityConfig.class})
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @Test
    public void getArticleTest() throws Exception {
        when(articleService.getArticle(1)).thenReturn(
                new Article(1, "Title1", "Subtitle1", "Content1")
        );

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/article/get/1")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("{articleId: 1,title:'Title1',subtitle:'Subtitle1',content:'Content1'}", JsonCompareMode.STRICT)) // verify/assert
                .andReturn();

    }
}
