package com.learningtohunt.web.server.repository;

import com.learningtohunt.web.server.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE com.learningtohunt.web.server.model.User u SET u.pwd = ?1 WHERE u.userId = ?2")
    int updateUserPwd(String pwd, int userId);
}
