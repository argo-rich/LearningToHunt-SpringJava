package com.learningtohunt.web.server.rest;

import com.learningtohunt.web.server.Application;
import com.learningtohunt.web.server.config.ProjectSecurityConfig;
import com.learningtohunt.web.server.config.TestConfig;
import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.model.UserRegistration;
import com.learningtohunt.web.server.model.UserToken;
import com.learningtohunt.web.server.security.JwtUtil;
import com.learningtohunt.web.server.security.PasswordResetUtil;
import com.learningtohunt.web.server.service.CustomUserDetailsService;
import com.learningtohunt.web.server.service.EmailService;
import com.learningtohunt.web.server.service.UserService;
import com.learningtohunt.web.server.service.UserTokenService;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private UserTokenService userTokenService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private PasswordResetUtil passwordResetUtil;

    private User user;
    private Authentication auth;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail(TestConfig.EMAIL);
        user.setEmailConfirmed(true);
        user.setFirstName("Bob");
        user.setLastName("Smith");

        auth = new TestingAuthenticationToken(TestConfig.EMAIL, TestConfig.PASSWORD_GOOD);

        userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
                passwordEncoder.encode(TestConfig.PASSWORD_GOOD), new ArrayList<>());
    }

    @Test
    public void loginTest_Success() throws Exception {
        auth.setAuthenticated(true);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(TestConfig.EMAIL,
                TestConfig.PASSWORD_GOOD))).thenReturn(auth);
        when(userService.findByEmail(TestConfig.EMAIL)).thenReturn(user);
        when(userDetailsService.loadUserByUsername(TestConfig.EMAIL)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("long.token.value");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + TestConfig.EMAIL + "\",\"password\":\"" + TestConfig.PASSWORD_GOOD + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("{userId: 1,email: '" + TestConfig.EMAIL + "',emailConfirmed: true,firstName: 'Bob',lastName: 'Smith'}")) // verify/assert
                .andReturn();
    }

    @Test
    public void loginTest_BadCredentials() throws Exception {
        auth.setAuthenticated(false);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(TestConfig.EMAIL, TestConfig.PASSWORD_BAD)))
                .thenReturn(auth);
        when(userService.findByEmail(TestConfig.EMAIL)).thenReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + TestConfig.EMAIL + "\",\"password\":\"" + TestConfig.PASSWORD_BAD + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andExpect(content().json("{\"statusCode\":\"500\",\"statusMsg\":\"Invalid credentials\"}", JsonCompareMode.STRICT)) // verify/assert
                .andReturn();
    }

    @Test
    public void logoutTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/account/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void pingTest_NoJwtToken() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/account/ping")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError()) // expect 403 error
                .andReturn();
    }

    @Test
    public void forgotPasswordTest() throws Exception {
        int resetCode = 123456;
        String resetToken = "3ibJq0N8PxJSUrAcdeQcVfZHVcNyPCuq";
        when(userService.findByEmail(TestConfig.EMAIL)).thenReturn(user);
        when(passwordResetUtil.generateResetCode()).thenReturn(resetCode);
        when(passwordResetUtil.generateToken()).thenReturn(resetToken);
        when(userTokenService.saveUserToken(resetToken, user.getUserId())).thenReturn(true);
        when(emailService.sendEmail(TestConfig.EMAIL, "Password Reset Code", "Your password reset code is: " + resetCode))
                .thenReturn(new MessageResponse());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + TestConfig.EMAIL + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().json("{resetCode:" + resetCode + ",token:\"" + resetToken + "\"}")) // verify/assert
                .andReturn();
    }

    @Test
    public void forgotPasswordTest_BlankContent() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void forgotPasswordTest_UnknownEmail() throws Exception {
        String bogusEmail = "bogus@email.com";
        when(userService.findByEmail(bogusEmail)).thenReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + bogusEmail + "\"}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andExpect(content().json("{\"statusCode\":\"500\",\"statusMsg\":\"User not found\"}")) // verify/assert
                .andReturn();
    }

    @Test
    public void forgotPasswordResetTest() throws Exception {
        String resetToken = "3ibJq0N8PxJSUrAcdeQcVfZHVcNyPCuq";
        UserToken userToken = new UserToken(resetToken, LocalDateTime.now().minusMinutes(5), user.getUserId(), false);
        when(userService.findByEmail(TestConfig.EMAIL)).thenReturn(user);
        when(userTokenService.findUserToken(resetToken, user.getUserId())).thenReturn(userToken);
        when(userService.updatePassword(TestConfig.PASSWORD_GOOD, user.getUserId())).thenReturn(true);
        doNothing().when(userTokenService).deleteUserToken(userToken);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/api/account/forgot-password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"resetToken\": \"" + resetToken + "\",\n" +
                        "  \"newPassword\": \"" + TestConfig.PASSWORD_GOOD + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void forgotPasswordResetTest_EmptyContent() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/api/account/forgot-password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void forgotPasswordResetTest_InvalidUser() throws Exception {
        String resetToken = "3ibJq0N8PxJSUrAcdeQcVfZHVcNyPCuq";
        UserToken userToken = new UserToken(resetToken, LocalDateTime.now().minusMinutes(5), user.getUserId(), false);
        when(userService.findByEmail(TestConfig.EMAIL)).thenReturn(null);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/api/account/forgot-password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"bogus@email.com\",\n" +
                        "  \"resetToken\": \"" + resetToken + "\",\n" +
                        "  \"newPassword\": \"" + TestConfig.PASSWORD_GOOD + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andReturn();
    }

    @Test
    public void forgotPasswordResetTest_TokenExpired() throws Exception {
        String resetToken = "3ibJq0N8PxJSUrAcdeQcVfZHVcNyPCuq";
        UserToken userToken = new UserToken(resetToken, LocalDateTime.now().minusMinutes(11), user.getUserId(), false);
        when(userService.findByEmail(TestConfig.EMAIL)).thenReturn(user);
        when(userTokenService.findUserToken(resetToken, user.getUserId())).thenReturn(userToken);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/api/account/forgot-password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"resetToken\": \"" + resetToken + "\",\n" +
                        "  \"newPassword\": \"" + TestConfig.PASSWORD_GOOD + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andReturn();
    }

    @Test
    public void registerTest_Success() throws Exception {
        when(userService.handleUserRegistration(any(UserRegistration.class))).thenReturn(true);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"firstName\": \"" + TestConfig.FIRST_NAME + "\",\n" +
                        "  \"lastName\": \"" + TestConfig.LAST_NAME + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void registerTest_Error() throws Exception {
        when(userService.handleUserRegistration(any(UserRegistration.class))).thenReturn(false);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"firstName\": \"" + TestConfig.FIRST_NAME + "\",\n" +
                        "  \"lastName\": \"" + TestConfig.LAST_NAME + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andExpect(content().json("{\"statusCode\":\"500\",\"statusMsg\":\"Failed to register user\"}"))
                .andReturn();
    }

    @Test
    public void registerTest_EmailInvalid() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"bogus\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"firstName\": \"" + TestConfig.FIRST_NAME + "\",\n" +
                        "  \"lastName\": \"" + TestConfig.LAST_NAME + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void registerTest_PwdNoSplChar() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_NO_SPL_CHAR + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_NO_SPL_CHAR + "\",\n" +
                        "  \"firstName\": \"" + TestConfig.FIRST_NAME + "\",\n" +
                        "  \"lastName\": \"" + TestConfig.LAST_NAME + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void registerTest_PwdConfirmPwdNoMatch() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_BAD + "\",\n" +
                        "  \"firstName\": \"" + TestConfig.FIRST_NAME + "\",\n" +
                        "  \"lastName\": \"" + TestConfig.LAST_NAME + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void registerTest_FirstNameBlank() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"firstName\": \"\",\n" +
                        "  \"lastName\": \"" + TestConfig.LAST_NAME + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void registerTest_FirstName101Chars() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"firstName\": \"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\",\n" +
                        "  \"lastName\": \"" + TestConfig.LAST_NAME + "\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void registerTest_LastNameBlank() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"firstName\": \"" + TestConfig.FIRST_NAME + "\",\n" +
                        "  \"lastName\": \"\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void registerTest_LastName101Chars() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"email\": \"" + TestConfig.EMAIL + "\",\n" +
                        "  \"password\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"confirmPassword\": \"" + TestConfig.PASSWORD_GOOD + "\",\n" +
                        "  \"firstName\": \"" + TestConfig.FIRST_NAME + "\",\n" +
                        "  \"lastName\": \"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    public void confirmationTest_Success() throws Exception {
        when(userService.handleRegistrationConfirmation(anyString())).thenReturn(true);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/account/confirmation/" + TestConfig.CONFIRMATION_URL_CODE);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(System.getenv("L2H_CLIENT_URL") + "/account/confirmation/success"))
                .andReturn();
    }

    @Test
    public void confirmationTest_TokenNotFound() throws Exception {
        when(userService.handleRegistrationConfirmation(anyString())).thenReturn(false);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/account/confirmation/" + TestConfig.CONFIRMATION_URL_CODE);

        mockMvc.perform(request)
                .andExpect(status().is5xxServerError())
                .andReturn();
    }
}