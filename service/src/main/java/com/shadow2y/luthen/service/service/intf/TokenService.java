package com.shadow2y.luthen.service.service.intf;

import com.shadow2y.luthen.api.SessionToken;

public interface TokenService {
    String generateToken();
    SessionToken createSessionToken(String userId, int durationMinutes);
    boolean validateToken(String token);
    void invalidateToken(String token);
    void invalidateAllUserTokens(String userId);
    void cleanupExpiredTokens();
}
