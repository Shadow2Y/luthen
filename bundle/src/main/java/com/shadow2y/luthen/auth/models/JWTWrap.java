package com.shadow2y.luthen.auth.models;

import com.nimbusds.jwt.JWTClaimsSet;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JWTWrap implements Principal {

    final JWTClaimsSet jwt;
    final Set<String> roles;

    @SuppressWarnings("unchecked")
    public JWTWrap(JWTClaimsSet claimsSet) {
        this.jwt = claimsSet;
        this.roles = new HashSet<>((List<String>) claimsSet.getClaims().get("roles")); /// TODO BitSet implementation after bundle client
    }

    @Override
    public String getName() {
        return jwt.getSubject();
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

}
