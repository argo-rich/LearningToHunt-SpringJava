package com.learningtohunt.web.server.service;

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
        when(userRepository.findByEmail("test@test.com")).thenReturn(
                new User(1, "test@test.com", true, "Bob", "Smith", "password", null)
        );

        User user = userService.findByEmail("test@test.com");

        assertEquals(1, user.getUserId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("Bob", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals(true, user.isEmailConfirmed());
    }
}
