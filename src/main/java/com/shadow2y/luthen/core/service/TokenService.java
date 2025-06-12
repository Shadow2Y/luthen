package com.shadow2y.luthen.core.service;

import com.shadow2y.luthen.core.model.SessionToken;

public interface TokenService {
    String generateToken();
    SessionToken createSessionToken(String userId, int durationMinutes);
    boolean validateToken(String token);
    void invalidateToken(String token);
    void invalidateAllUserTokens(String userId);
    void cleanupExpiredTokens();
}
