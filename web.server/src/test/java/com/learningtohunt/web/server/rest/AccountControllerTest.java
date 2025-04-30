package com.learningtohunt.web.server.rest;

import com.learningtohunt.web.server.Application;
import com.learningtohunt.web.server.config.ProjectSecurityConfig;
import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes={Application.class, ProjectSecurityConfig.class})
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserService userService;

    private User user;
    private Authentication auth;
    private String goodPassword = "Abc123!";
    private String badPassword = "Def456!";

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail("test@test.com");
        user.setEmailConfirmed(true);
        user.setFirstName("Bob");
        user.setLastName("Smith");

        auth = new TestingAuthenticationToken("test@test.com", goodPassword);
    }

    @Test
    public void loginTest_Success() throws Exception {
        auth.setAuthenticated(true);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("test@test.com", goodPassword)))
                .thenReturn(auth);
        when(userService.findByEmail("test@test.com")).thenReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"" + goodPassword + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("{userId: 1,email: 'test@test.com',emailConfirmed: true,firstName: 'Bob',lastName: 'Smith'}", JsonCompareMode.STRICT)) // verify/assert
                .andReturn();
    }

    @Test
    public void loginTest_BadCredentials() throws Exception {
        auth.setAuthenticated(false);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("test@test.com", badPassword)))
                .thenReturn(auth);
        when(userService.findByEmail("test@test.com")).thenReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"" + badPassword + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andExpect(content().json("{\"statusCode\":\"500\",\"statusMsg\":\"Invalid credentials\"}", JsonCompareMode.STRICT)) // verify/assert
                .andReturn();
    }
}
