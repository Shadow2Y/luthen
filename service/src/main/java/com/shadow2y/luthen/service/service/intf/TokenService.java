package com.shadow2y.luthen.service.service.intf;

import com.shadow2y.luthen.api.models.auth.UserAuth;

public interface TokenService {
    String generateToken();
    String createAccessToken(UserAuth userAuth);
    boolean verifyAccessToken(String token);
    void invalidateToken(String token);
    void invalidateAllUserTokens(String userId);
    void cleanupExpiredTokens();
}
