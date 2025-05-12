package com.learningtohunt.web.server.rest;

import com.learningtohunt.web.server.model.*;
import com.learningtohunt.web.server.security.JwtUtil;
import com.learningtohunt.web.server.security.PasswordResetUtil;
import com.learningtohunt.web.server.service.CustomUserDetailsService;
import com.learningtohunt.web.server.service.EmailService;
import com.learningtohunt.web.server.service.UserService;
import com.learningtohunt.web.server.service.UserTokenService;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(path = "/api/account",produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetUtil passwordResetUtil;

    @Autowired
    private Environment environment;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(@RequestBody Credentials creds) throws Exception {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword()));

        if (auth.isAuthenticated()) {
            User user = userService.findByEmail(creds.getEmail());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(creds.getEmail());
            user.setToken(jwtUtil.generateToken(userDetails));
            return user;
        }

        throw new Exception("Invalid credentials");
    }

    @RequestMapping(value="/logout", method = RequestMethod.PUT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        request.getSession().invalidate();
    }

    @RequestMapping(path = "/ping", method = {RequestMethod.GET})
    public void ping() {}

    @RequestMapping(path = "/forgot-password", method = {RequestMethod.POST})
    public ResetTokensResponse forgotPassword(@RequestBody Credentials creds) throws Exception {
        if (creds != null) {
            User user = userService.findByEmail(creds.getEmail());
            if (user != null) {
                int resetCode = passwordResetUtil.generateResetCode();
                String token = passwordResetUtil.generateToken();
                boolean success = userTokenService.saveUserToken(token, user.getUserId());
                if (success) {
                    MessageResponse response = emailService.sendEmail(creds.getEmail(), "Password Reset Code", "Your password reset code is: " + resetCode);
                    log.info("MessageResponse: " + response.getMessage());
                    return new ResetTokensResponse(resetCode, token);
                }
            } else {
                throw new Exception("User not found");
            }
        }

        return null;
    }

    @RequestMapping(path = "/forgot-password-reset", method = {RequestMethod.PATCH})
    public void updateForgottenPassword(@RequestBody UpdateForgottenPassword updateForgottenPassword) throws Exception {
        boolean validData = true;
        User user = userService.findByEmail(updateForgottenPassword.getEmail());

        if (user != null) {
            UserToken userToken = userTokenService.findUserToken(updateForgottenPassword.getResetToken(), user.getUserId());
            int expireMinutes = environment.getProperty("learningtohunt.resetToken.expire.minutes", Integer.class);

            if (userToken != null &&
                    LocalDateTime.now().isBefore(userToken.getTokenTimestamp().plusMinutes(expireMinutes))) {
                // update password
                user.setPwd(updateForgottenPassword.getNewPassword());
                boolean success = userService.updatePassword(updateForgottenPassword.getNewPassword(), user.getUserId());

                // update user token
                if (success) {
                    userTokenService.deleteUserToken(userToken);
                } else {
                    throw new Exception("Failed to update password");
                }
            } else {
                validData = false;
            }
        } else {
            validData = false;
        }

        if (!validData) {
            throw new Exception("Invalid data");
        }
    }

    @RequestMapping(path = "/register", method = {RequestMethod.POST})
    public void register(@Valid @RequestBody UserRegistration userReg) throws Exception {
        boolean success = userService.handleUserRegistration(userReg);
        if (!success) {
            throw new Exception("Failed to register user");
        }
    }

    @RequestMapping(path = "/confirmation/{regToken}", method = {RequestMethod.GET})
    public void registerConfirmation(@PathVariable(name = "regToken") String regToken, HttpServletResponse response) throws Exception {
        if (userService.handleRegistrationConfirmation(regToken)) {
            response.sendRedirect(System.getenv("L2H_CLIENT_URL") + "/account/confirmation/success");
        } else {
            throw new Exception("Failed to confirm registration");
        }
    }

    @RequestMapping(path = "/update", method = {RequestMethod.PUT})
    public void update(@Valid @RequestBody UserUpdate userUpdate) throws Exception {
        boolean success = userService.handleUserUpdate(userUpdate);
        if (!success) {
            throw new Exception("Failed to update user");
        }
    }
}
