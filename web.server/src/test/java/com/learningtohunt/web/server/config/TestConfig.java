package com.learningtohunt.web.server.config;

import java.util.Base64;

public class TestConfig {
    public static final String EMAIL = "test@test.com";
    public static final String PASSWORD_GOOD = "Abc123!";
    public static final String PASSWORD_NO_SPL_CHAR = "Abc1239";
    public static final String PASSWORD_BAD = "XXXXXXX";
    public static final String FIRST_NAME = "Bob";
    public static final String LAST_NAME = "Smith";
    public static final String CONFIRMATION_URL_CODE = "RFBCV09kbVFyZjFOVnlQQVVvbTN2Qzk4YUxHRVppZ25pUHg=";
    public static final String CONFIRMATION_CODE = new String(Base64.getDecoder().decode(TestConfig.CONFIRMATION_URL_CODE));
}