package com.shadow2y.luthen.service.service.intf;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shadow2y.commons.Result;
import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.service.exception.LuthenError;

public interface TokenService {
    SignedJWT createAccessToken(UserSummary userSummary);
    boolean verifyAccessToken(String token);
    Result<JWTClaimsSet,LuthenError> validateGetClaims(String token);
    void invalidateToken(String token);
    void invalidateAllUserTokens(String userId);
}
