package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.config.TestConfig;
import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService  userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void getUserTest() {
        when(userRepository.findByEmail(TestConfig.EMAIL)).thenReturn(
                new User(1, TestConfig.EMAIL, true, "Bob", "Smith", TestConfig.GOOD_PASSWORD, null)
        );

        User user = userService.findByEmail("test@test.com");

        assertEquals(1, user.getUserId());
        assertEquals(TestConfig.EMAIL, user.getEmail());
        assertEquals(TestConfig.GOOD_PASSWORD, user.getPwd());
        assertEquals("Bob", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals(true, user.isEmailConfirmed());
    }

    /**
     * No way to test this without actually changing passwords in the database.
     */
    @Test
    public void updatePasswordTest() { }
}
