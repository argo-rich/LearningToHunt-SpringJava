package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.model.UserToken;
import com.learningtohunt.web.server.repository.UserTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserTokenServiceTest {

    @InjectMocks
    private UserTokenService userTokenService;

    @Mock
    private UserTokenRepository userTokenRepository;

    private String resetToken;
    int userId;
    UserToken userToken;
    private LocalDateTime tokenTimestamp;

    @BeforeEach
    public void setUp() {
        resetToken = "3ibJq0N8PxJSUrAcdeQcVfZHVcNyPCuq";
        userId = 1;
        tokenTimestamp = LocalDateTime.now();
        userToken = new UserToken(resetToken, tokenTimestamp, userId, false);
        userToken.setCreatedAt(LocalDateTime.now());
        userToken.setCreatedBy("unitTest");
    }

    @Test
    public void saveUserTokenTest() {
        when(userTokenRepository.save(any(UserToken.class))).thenReturn(userToken);

        boolean result = userTokenService.saveUserToken(resetToken, userId);

        assertTrue(result);
    }

    @Test
    public void saveUserTokenTest_CreatedByNull() {
        userToken.setCreatedAt(null);
        userToken.setCreatedBy(null);
        when(userTokenRepository.save(any(UserToken.class))).thenReturn(userToken);

        boolean result = userTokenService.saveUserToken(resetToken, userId);

        assertFalse(result);
    }

    @Test
    public void findUserTokenTest() {
        when(userTokenRepository.findDistinctByTokenAndUserId(resetToken, userId)).thenReturn(userToken);

        UserToken foundToken = userTokenService.findUserToken(resetToken, userId);

        assertEquals(resetToken, foundToken.getToken());
        assertEquals(userId, foundToken.getUserId());
    }

    /**
     * Purposely not implemented since there is no way to test this without actually deleting tokens in the database.
     */
    @Test
    public void deleteUserTokenTest() { }
}
