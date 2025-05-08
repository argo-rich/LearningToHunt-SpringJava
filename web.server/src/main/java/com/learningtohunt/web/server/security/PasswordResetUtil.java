package com.learningtohunt.web.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PasswordResetUtil {

    @Autowired
    private Environment environment;

    public String generateToken() {
        return this.generateToken(environment.getProperty("learningtohunt.resetToken.length", Integer.class));
    }

    public String generateToken(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public int generateResetCode() {
        return new Random().nextInt(100000, 1000000);
    }
}
