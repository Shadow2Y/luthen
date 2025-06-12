package com.shadow2y.luthen.core.repository;

import com.shadow2y.luthen.core.model.SessionToken;

import java.util.List;
import java.util.Optional;

public interface SessionTokenRepository {
    SessionToken save(SessionToken token);
    Optional<SessionToken> findByToken(String token);
    List<SessionToken> findByUserId(String userId);
    void deleteByToken(String token);
    void deleteByUserId(String userId);
    void deleteExpiredTokens();
}
