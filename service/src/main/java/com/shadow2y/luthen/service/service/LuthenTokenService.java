package com.shadow2y.luthen.service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.*;
import com.shadow2y.luthen.api.response.UserAuth;
import com.shadow2y.luthen.service.service.intf.TokenService;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class LuthenTokenService implements TokenService {

    private final String issuer;
    private final JWSSigner signer;
    private final JWSHeader header;
    private final long accessMinutes;
    private final RSAPublicKey publicKey;

    public LuthenTokenService(RSAPrivateKey privateKey, RSAPublicKey publicKey, String issuer, long accessMinutes) {
        this.issuer = issuer;
        this.publicKey = publicKey;
        this.accessMinutes = accessMinutes;
        this.signer = new RSASSASigner(privateKey);
        this.header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
    }

    public UserAuth createAccessToken(UserAuth userAuth) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessMinutes * 60);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(userAuth.getUsername())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", userAuth.getRoles())
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        userAuth.setAccessToken(signedJWT.serialize()).setCreatedAt(now).setExpiresAt(exp);
        return userAuth;
    }

    public boolean verifyAccessToken(String token) {
        try {
            validateGetClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private JWTClaimsSet validateGetClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!signedJWT.verify(verifier)) {
                throw new SecurityException("Invalid signature");
            }
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date now = new Date();
            if (claims.getExpirationTime() == null || claims.getExpirationTime().before(now)) {
                throw new SecurityException("Token expired");
            }
            return claims;
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public boolean verifyAccessTokenRole(String token, String role) {
        try {
            if (!verifyAccessToken(token)) return false;
            var claims = validateGetClaims(token);
            return claims.getBooleanClaim(role);
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public String generateToken() {
        return String.format("%s-%s",UUID.randomUUID(), System.currentTimeMillis());
    }

    @Override
    public void invalidateToken(String token) {
//        sessionStore.deleteByToken(token);
    }

    @Override
    public void invalidateAllUserTokens(String userId) {
//        sessionStore.deleteByUserId(userId);
    }

    @Override
    public void cleanupExpiredTokens() {
//        sessionStore.deleteExpiredTokens();
    }
}
