package com.shadow2y.luthen.auth;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shadow2y.luthen.auth.models.JWTWrap;
import io.dropwizard.auth.Authenticator;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.ECPublicKey;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class LuthenAuthenticator implements Authenticator<String, JWTWrap> {

    private final String expectedIssuer;
    private final ECPublicKey publicKey;

    public LuthenAuthenticator(ECPublicKey publicKey, String expectedIssuer) {
        this.publicKey = publicKey;
        this.expectedIssuer = expectedIssuer;
    }

    @Override
    public Optional<JWTWrap> authenticate(String token) {
        try {
            var jwt = getClaims(token);
            return Optional.of(new JWTWrap(jwt));
        } catch (Exception e) {
            log.error("Exception occurred while authenticating :: {}",e.getMessage());
            return Optional.empty();
        }
    }

    public JWTClaimsSet getClaims(String token) throws Exception {
        SignedJWT jwt = SignedJWT.parse(token);
        JWSVerifier verifier = new ECDSAVerifier(publicKey);
        if (!jwt.verify(verifier)) {
            throw new SecurityException("Invalid signature");
        }

        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        Date now = new Date();
        if (claims.getExpirationTime() == null || claims.getExpirationTime().before(now)) {
            throw new SecurityException("Token expired");
        }
        if (!expectedIssuer.equals(claims.getIssuer())) {
            throw new SecurityException("Invalid issuer");
        }
        return claims;
    }

}

