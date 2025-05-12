package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService  customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void loadUserByUsernameTest() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(
                new User(1, "test@test.com", true, "Bob", "Smith", "password", null, new HashSet<>())
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@test.com");

        assertEquals("test@test.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(true, userDetails.isEnabled());
    }
}
