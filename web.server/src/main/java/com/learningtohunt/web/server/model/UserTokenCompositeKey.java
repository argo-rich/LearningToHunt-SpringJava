package com.learningtohunt.web.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenCompositeKey implements Serializable {
    private String token;
    private LocalDateTime tokenTimestamp;
}