package com.shadow2y.luthen.service.service.intf;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shadow2y.commons.Result;
import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.service.exception.LuthenError;

import java.util.Optional;

public interface TokenService {
    SignedJWT createAccessToken(UserSummary userSummary);
    boolean verifyAccessToken(String token);
    Result<JWTClaimsSet,LuthenError> validateGetClaims(String token);
    Optional<JWTClaimsSet> getJwt(String token);
}
