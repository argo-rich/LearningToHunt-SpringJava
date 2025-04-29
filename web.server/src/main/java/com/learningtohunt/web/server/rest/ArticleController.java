package com.learningtohunt.web.server.rest;

import com.learningtohunt.web.server.model.Article;
import com.learningtohunt.web.server.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(path = "/api/article",produces = {MediaType.APPLICATION_JSON_VALUE})
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/get/{id}")
    //@ResponseBody
    public Article getMessagesByStatus(@PathVariable(name = "id")  int id) {
        Article article = articleService.getArticle(id);
        return article;
    }
}
