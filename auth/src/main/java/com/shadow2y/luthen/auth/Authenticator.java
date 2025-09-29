package com.shadow2y.luthen.auth;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shadow2y.luthen.api.response.UserAuth;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Optional;

public class Authenticator implements io.dropwizard.auth.Authenticator<String, UserAuth> {

    private final String expectedIssuer;
    private final RSAPublicKey publicKey;

    public Authenticator(RSAPublicKey publicKey, String expectedIssuer) {
        this.publicKey = publicKey;
        this.expectedIssuer = expectedIssuer;
    }

    @Override
    public Optional<UserAuth> authenticate(String token) {
        try {
            var jwt = getClaims(token);
            var user = new UserAuth(jwt.getSubject(), jwt.getStringListClaim("roles"));
            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public JWTClaimsSet getClaims(String token) throws Exception {
        SignedJWT jwt = SignedJWT.parse(token);
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
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

