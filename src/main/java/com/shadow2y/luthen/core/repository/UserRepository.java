package com.shadow2y.luthen.core.repository;

import com.shadow2y.luthen.core.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void deleteById(String userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
