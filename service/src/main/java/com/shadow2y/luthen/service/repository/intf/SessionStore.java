package com.shadow2y.luthen.service.repository.intf;

import com.shadow2y.luthen.api.SessionToken;

import java.util.List;
import java.util.Optional;

public interface SessionStore {
    SessionToken save(SessionToken token);
    Optional<SessionToken> findByToken(String token);
    List<SessionToken> findByUserId(String userId);
    void deleteByToken(String token);
    void deleteByUserId(String userId);
    void deleteExpiredTokens();
}
