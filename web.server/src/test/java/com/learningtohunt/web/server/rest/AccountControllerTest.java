package com.learningtohunt.web.server.rest;

import com.learningtohunt.web.server.Application;
import com.learningtohunt.web.server.config.ProjectSecurityConfig;
import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.security.JwtUtil;
import com.learningtohunt.web.server.service.CustomUserDetailsService;
import com.learningtohunt.web.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes={Application.class, ProjectSecurityConfig.class})
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private User user;
    private Authentication auth;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail(AcctTstConfig.EMAIL);
        user.setEmailConfirmed(true);
        user.setFirstName("Bob");
        user.setLastName("Smith");

        auth = new TestingAuthenticationToken(AcctTstConfig.EMAIL, AcctTstConfig.GOOD_PASSWORD);

        userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
                passwordEncoder.encode(AcctTstConfig.GOOD_PASSWORD), new ArrayList<>());
    }

    @Test
    public void loginTest_Success() throws Exception {
        auth.setAuthenticated(true);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(AcctTstConfig.EMAIL,
                AcctTstConfig.GOOD_PASSWORD))).thenReturn(auth);
        when(userService.findByEmail(AcctTstConfig.EMAIL)).thenReturn(user);
        when(userDetailsService.loadUserByUsername(AcctTstConfig.EMAIL)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("long.token.value");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + AcctTstConfig.EMAIL + "\",\"password\":\"" + AcctTstConfig.GOOD_PASSWORD + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("{userId: 1,email: '" + AcctTstConfig.EMAIL + "',emailConfirmed: true,firstName: 'Bob',lastName: 'Smith'}")) // verify/assert
                .andReturn();
    }

    @Test
    public void loginTest_BadCredentials() throws Exception {
        auth.setAuthenticated(false);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(AcctTstConfig.EMAIL, AcctTstConfig.BAD_PASSWORD)))
                .thenReturn(auth);
        when(userService.findByEmail(AcctTstConfig.EMAIL)).thenReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + AcctTstConfig.EMAIL + "\",\"password\":\"" + AcctTstConfig.BAD_PASSWORD + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andExpect(content().json("{\"statusCode\":\"500\",\"statusMsg\":\"Invalid credentials\"}", JsonCompareMode.STRICT)) // verify/assert
                .andReturn();
    }
}

class AcctTstConfig {
    static final String EMAIL = "test@test.com";
    static final String GOOD_PASSWORD = "Abc123!";
    static final String BAD_PASSWORD = "Def456!";
}