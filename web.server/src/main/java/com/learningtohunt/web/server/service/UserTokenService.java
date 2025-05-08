package com.learningtohunt.web.server.service;

import com.learningtohunt.web.server.model.UserToken;
import com.learningtohunt.web.server.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserTokenService {

    @Autowired
    private UserTokenRepository userTokenRepository;

    /**
     *  Saves a new user token to the database.
     * @param token The token to be saved
     * @param userId The id of the user the token belongs to
     * @return True if the token was saved, false otherwise
     */
    public boolean saveUserToken(String token, int userId) {
        UserToken savedUserToken = userTokenRepository.save(new UserToken(token, LocalDateTime.now(), userId, false));
        return (null != savedUserToken && savedUserToken.getCreatedBy() != null);
    }

    public UserToken findUserToken(String token) {
        return userTokenRepository.findDistinctByToken(token);
    }

    public UserToken findUserToken(String token, int userId) {
        return userTokenRepository.findDistinctByTokenAndUserId(token, userId);
    }

    public void deleteUserToken(UserToken userToken) {
        userTokenRepository.delete(userToken);
    }
}
