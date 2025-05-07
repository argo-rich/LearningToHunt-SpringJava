package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.model.User;
import com.learningtohunt.web.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean updatePassword(String newPwd, int userId) {
        int rowsUpdated = userRepository.updateUserPwd(passwordEncoder.encode(newPwd), userId);
        return rowsUpdated == 1;
    }
}
