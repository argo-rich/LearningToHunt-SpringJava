package com.learningtohunt.web.server.rest;

import com.learningtohunt.web.server.Application;
import com.learningtohunt.web.server.config.ProjectSecurityConfig;
import com.learningtohunt.web.server.config.TestConfig;
import com.learningtohunt.web.server.model.Article;
import com.learningtohunt.web.server.security.JwtUtil;
import com.learningtohunt.web.server.service.ArticleService;
import com.learningtohunt.web.server.service.CustomUserDetailsService;
import com.learningtohunt.web.server.service.EmailService;
import com.learningtohunt.web.server.service.UserTokenService;
import com.postmarkapp.postmark.client.data.model.message.Message;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

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

    // only used by Spring WebMvcTest
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private EmailService emailService;

    // only used by Spring WebMvcTest
    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() throws Exception {
        /*
        when(customUserDetailsService.loadUserByUsername(TestConfig.EMAIL)).thenReturn(
            new org.springframework.security.core.userdetails.User(TestConfig.EMAIL, TestConfig.GOOD_PASSWORD, new ArrayList<>())
        );

        when(emailService.sendEmail(TestConfig.EMAIL, "Title1", "Content1")).thenReturn(
                new MessageResponse()
        );
         */
    }

    @Test
    public void getArticleTest() throws Exception {
        when(articleService.getArticle(1)).thenReturn(
                new Article(1, "Title1", "Subtitle1", "Content1")
        );

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/article/get/1")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("{articleId: 1,title:'Title1',subtitle:'Subtitle1',content:'Content1'}", JsonCompareMode.STRICT)) // verify/assert
                .andReturn();

    }
}
