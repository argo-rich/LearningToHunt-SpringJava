package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.model.Article;
import com.learningtohunt.web.server.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Article getArticle(int id) {
        return articleRepository.findById(id).orElse(new Article());
    }
}
