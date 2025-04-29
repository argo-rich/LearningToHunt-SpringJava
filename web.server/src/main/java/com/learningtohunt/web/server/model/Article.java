package com.learningtohunt.web.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "articles")
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native")
    private int articleId;

    @NotBlank(message="Title must not be blank")
    @Size(max=25, message="Title must be 25 characters or less")
    private String title;

    @NotBlank(message="Subtitle must not be blank")
    @Size(max=25, message="Subtitle must be 35 characters or less")
    private String subtitle;

    @NotBlank(message="Content must not be blank")
    private String content;
}
