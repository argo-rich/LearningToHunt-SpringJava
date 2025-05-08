package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.config.TestConfig;
import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.model.UserRegistration;
import com.learningtohunt.web.server.model.UserToken;
import com.learningtohunt.web.server.repository.UserRepository;
import com.learningtohunt.web.server.security.PasswordResetUtil;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService  userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetUtil passwordResetUtil;

    @Mock
    private EmailService emailService;

    @Test
    public void getUserTest() {
        when(userRepository.findByEmail(TestConfig.EMAIL)).thenReturn(
                new User(1, TestConfig.EMAIL, true, "Bob", "Smith", TestConfig.PASSWORD_GOOD, null)
        );

        User user = userService.findByEmail("test@test.com");

        assertEquals(1, user.getUserId());
        assertEquals(TestConfig.EMAIL, user.getEmail());
        assertEquals(TestConfig.PASSWORD_GOOD, user.getPwd());
        assertEquals("Bob", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals(true, user.isEmailConfirmed());
    }

    /**
     * No way to test this without actually changing passwords in the database.
     */
    @Test
    public void updatePasswordTest() { }

    @Test
    public void handleUserRegistrationTest_Success() throws Exception {
        UserRegistration userReg = new UserRegistration(TestConfig.EMAIL, TestConfig.PASSWORD_GOOD,
                TestConfig.PASSWORD_GOOD, TestConfig.FIRST_NAME, TestConfig.LAST_NAME);

        when(userRepository.save(any(User.class))).thenReturn(
            new User(1, userReg.getEmail(), true, userReg.getFirstName(), userReg.getLastName(), userReg.getPassword(),
                    null));
        when(passwordEncoder.encode(anyString())).thenReturn(TestConfig.PASSWORD_GOOD);
        when(passwordResetUtil.generateToken()).thenReturn("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        when(userTokenService.saveUserToken(anyString(), anyInt())).thenReturn(true);
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(new MessageResponse());

        boolean success = userService.handleUserRegistration(userReg);

        assertTrue(success);
    }

    @Test
    public void handleUserRegistrationTest_NullUser() throws Exception {
        UserRegistration userReg = new UserRegistration(TestConfig.EMAIL, TestConfig.PASSWORD_GOOD,
                TestConfig.PASSWORD_GOOD, TestConfig.FIRST_NAME, TestConfig.LAST_NAME);

        when(userRepository.save(any(User.class))).thenReturn(null);

        boolean success = userService.handleUserRegistration(userReg);

        assertFalse(success);
    }

    @Test
    public void handleUserRegistrationTest_UserIdIsZero() throws Exception {
        UserRegistration userReg = new UserRegistration(TestConfig.EMAIL, TestConfig.PASSWORD_GOOD,
                TestConfig.PASSWORD_GOOD, TestConfig.FIRST_NAME, TestConfig.LAST_NAME);

        when(userRepository.save(any(User.class))).thenReturn(
                new User(0, userReg.getEmail(), true, userReg.getFirstName(), userReg.getLastName(),
                        userReg.getPassword(), null)
        );

        boolean success = userService.handleUserRegistration(userReg);

        assertFalse(success);
    }

    @Test
    public void handleUserRegistrationTest_TokenNotSaved() throws Exception {
        UserRegistration userReg = new UserRegistration(TestConfig.EMAIL, TestConfig.PASSWORD_GOOD,
                TestConfig.PASSWORD_GOOD, TestConfig.FIRST_NAME, TestConfig.LAST_NAME);

        when(userRepository.save(any(User.class))).thenReturn(
                new User(1, userReg.getEmail(), true, userReg.getFirstName(), userReg.getLastName(), userReg.getPassword(),
                        null));
        when(passwordEncoder.encode(anyString())).thenReturn(TestConfig.PASSWORD_GOOD);
        when(passwordResetUtil.generateToken()).thenReturn("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        when(userTokenService.saveUserToken(anyString(), anyInt())).thenReturn(false);

        boolean success = userService.handleUserRegistration(userReg);

        assertFalse(success);
    }

    @Test
    public void handleRegistrationConfirmation_Success() throws Exception {
        UserToken userToken = new UserToken(TestConfig.CONFIRMATION_CODE, LocalDateTime.now(), 1, false);
        User user = new User(userToken.getUserId(), TestConfig.EMAIL, false, TestConfig.FIRST_NAME, TestConfig.LAST_NAME, TestConfig.PASSWORD_GOOD, null);
        when(userTokenService.findUserToken(TestConfig.CONFIRMATION_CODE)).thenReturn(userToken);
        when(userRepository.findByUserId(1)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(userTokenService).deleteUserToken(any(UserToken.class));

        // call handleRegistrationConfirmation()
        boolean success = userService.handleRegistrationConfirmation(TestConfig.CONFIRMATION_URL_CODE);

        // assert
        assertTrue(success);
        assertEquals(true, user.isEmailConfirmed());
    }

    @Test
    public void handleRegistrationConfirmation_TokenNotFound() throws Exception {
        when(userTokenService.findUserToken(TestConfig.CONFIRMATION_CODE)).thenReturn(null);

        // call handleRegistrationConfirmation()
        boolean success = userService.handleRegistrationConfirmation(TestConfig.CONFIRMATION_URL_CODE);

        // assert
        assertFalse(success);
    }

    @Test
    public void handleRegistrationConfirmation_UserNotFound() throws Exception {
        UserToken userToken = new UserToken(TestConfig.CONFIRMATION_CODE, LocalDateTime.now(), 1, false);
        when(userTokenService.findUserToken(TestConfig.CONFIRMATION_CODE)).thenReturn(userToken);
        when(userRepository.findByUserId(1)).thenReturn(null);

        // call handleRegistrationConfirmation()
        boolean success = userService.handleRegistrationConfirmation(TestConfig.CONFIRMATION_URL_CODE);

        // assert
        assertFalse(success);
    }
}
