package com.learningtohunt.web.server.repository;

import com.learningtohunt.web.server.model.UserToken;
import com.learningtohunt.web.server.model.UserTokenCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UserTokenCompositeKey> {
    UserToken findDistinctByTokenAndUserId(String token, int userId);
}
