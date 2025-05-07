package com.learningtohunt.web.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a token used to reset a forgotten password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_tokens")
@IdClass(UserTokenCompositeKey.class)
public class UserToken extends BaseEntity {

    @Id
    private String token;

    @Id
    private LocalDateTime tokenTimestamp;

    private int userId;

    private boolean tokenConfirmed;
}
