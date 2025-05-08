package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.model.UserRegistration;
import com.learningtohunt.web.server.model.UserToken;
import com.learningtohunt.web.server.repository.UserRepository;
import com.learningtohunt.web.server.security.PasswordResetUtil;
import com.postmarkapp.postmark.client.exception.PostmarkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private PasswordResetUtil passwordResetUtil;

    @Autowired
    private EmailService emailService;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean updatePassword(String newPwd, int userId) {
        int rowsUpdated = userRepository.updateUserPwd(passwordEncoder.encode(newPwd), userId);
        return rowsUpdated == 1;
    }

    public boolean handleUserRegistration(UserRegistration userReg) throws PostmarkException, IOException {
        User savedUser = userRepository.save(convertUserRegistrationToUser(userReg));
        if (savedUser != null && savedUser.getUserId() > 0) {
            // generate email confirmation token
            String token = passwordResetUtil.generateToken();
            boolean tokenSaved = userTokenService.saveUserToken(token, savedUser.getUserId());

            // send confirm email with token
            if (tokenSaved) {
                emailService.sendEmail(userReg.getEmail(), "Confirm your email",
                        "Please confirm your account by <a href=\"" + System.getenv("L2H_API_URL") +
                                "/api/account/confirmation/" + Base64.getEncoder().encodeToString(token.getBytes()) +
                                "\">clicking here</a>.");
                return true;
            }
        }

        return false;
    }

    public boolean handleRegistrationConfirmation(String urlToken) {
        String token = new String(Base64.getDecoder().decode(urlToken));
        UserToken userToken = userTokenService.findUserToken(token);
        if (userToken != null) {
            User user = userRepository.findByUserId(userToken.getUserId());
            if (user != null) {
                user.setEmailConfirmed(true);
                userRepository.save(user);
                userTokenService.deleteUserToken(userToken);
                return true;
            }
        }

        return false;
    }

    private User convertUserRegistrationToUser(UserRegistration userReg) {
        User user = new User();
        user.setEmail(userReg.getEmail());
        user.setFirstName(userReg.getFirstName());
        user.setLastName(userReg.getLastName());
        user.setPwd(passwordEncoder.encode(userReg.getPassword()));

        return user;
    }
}
