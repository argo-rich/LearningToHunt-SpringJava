package com.learningtohunt.web.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetTokensResponse {
    private int resetCode;
    private String token;
}
