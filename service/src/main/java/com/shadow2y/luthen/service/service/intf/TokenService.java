package com.shadow2y.luthen.service.service.intf;

import com.nimbusds.jwt.JWTClaimsSet;
import com.shadow2y.luthen.api.response.UserAuth;
import com.shadow2y.luthen.service.exception.LuthenError;

public interface TokenService {
    UserAuth createAccessToken(UserAuth userAuth);
    boolean verifyAccessToken(String token);
    JWTClaimsSet validateGetClaims(String token) throws LuthenError;
    void invalidateToken(String token);
    void invalidateAllUserTokens(String userId);
}
