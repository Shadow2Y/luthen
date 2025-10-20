package com.shadow2y.luthen.auth;

import com.shadow2y.luthen.api.models.UserAuth;
import com.shadow2y.luthen.auth.models.JWTWrap;
import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RoleAuthorizer implements Authorizer<JWTWrap> {

    @Override
    public boolean authorize(JWTWrap jwtWrap, String requiredRole, @Nullable ContainerRequestContext containerRequestContext) {
        return jwtWrap.hasRole(requiredRole);
    }

}

