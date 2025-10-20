package com.shadow2y.luthen.service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.*;
import com.shadow2y.luthen.api.models.UserAuth;
import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.model.Result;
import com.shadow2y.luthen.service.service.intf.TokenService;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.relation.RelationSupport;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class LuthenTokenService implements TokenService {

    private final String issuer;
    private final JWSSigner signer;
    private final JWSHeader header;
    private final JWSVerifier verifier;
    private final long accessMinutes;

    private static final Logger log = LoggerFactory.getLogger(LuthenTokenService.class);

    @Inject
    public LuthenTokenService(RSAPrivateKey privateKey, RSAPublicKey publicKey, String issuer, long accessMinutes) {
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
        this.signer = new RSASSASigner(privateKey);
        this.verifier = new RSASSAVerifier(publicKey);
        this.header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
    }

    @Override
    public SignedJWT createAccessToken(UserSummary user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessMinutes * 60);

        log.info("Creating access token for user :: {}", user.username());

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(user.username())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", user.roles())
                .build();

        log.debug("Created claims: {}", claims.toJSONObject());

        SignedJWT signedJWT = new SignedJWT(header, claims);
        try {
            signedJWT.sign(signer);
            log.info("Created access token for user :: {}, with expiration :: {}", user.username(), exp);
            return signedJWT;
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyAccessToken(String token) {
        try {
            validateGetClaims(token);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean verifyAccessTokenRole(String token, String role) {
        try {
            if (!verifyAccessToken(token)) return false;
            var claims = validateGetClaims(token).throwIfError().get();
            List<String> roles = claims.getStringListClaim("roles");
            return roles != null && roles.contains(role);
        } catch (Throwable e) {
            log.error("Unable to verify token :: ",e);
            return false;
        }
    }

    @Override
    public Result<JWTClaimsSet,LuthenError> validateGetClaims(String token) {
        try {
            log.debug("Validating token: {}", token);
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(verifier)) {
                return Result.error(new LuthenError(Error.INVALID_TOKEN_SIGNATURE));
            }

            Result<JWTClaimsSet, LuthenError> result = new Result<>();
            Date now = new Date();
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            if (claims.getExpirationTime() == null || claims.getExpirationTime().before(now)) {
                log.error("Token expired. Expiration: {}, Current time: {}", claims.getExpirationTime(), now);
                result.setError(new LuthenError(Error.TOKEN_EXPIRED));
            }
            result.set(claims);
            return result;
        } catch (JOSEException | ParseException e) {
            log.error("Token validation failed", e);
            return Result.error(new LuthenError(Error.TOKEN_AUTHENTICATION_FAILED, e));
        }
    }

    @Override
    public void invalidateToken(String token) {
//        sessionStore.deleteByToken(token);
    }

    @Override
    public void invalidateAllUserTokens(String userId) {
//        sessionStore.deleteByUserId(userId);
    }

}
