package com.shadow2y.luthen.service.service.intf;

import com.shadow2y.luthen.api.response.UserAuth;

public interface TokenService {
    String generateToken();
    UserAuth createAccessToken(UserAuth userAuth);
    boolean verifyAccessToken(String token);
    void invalidateToken(String token);
    void invalidateAllUserTokens(String userId);
    void cleanupExpiredTokens();
}
