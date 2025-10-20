package com.shadow2y.luthen.auth;

import com.shadow2y.luthen.auth.models.JWTWrap;
import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;


@Slf4j
public class RoleAuthorizer implements Authorizer<JWTWrap> {

    @Override
    public boolean authorize(JWTWrap jwtWrap, String requiredRole, @Nullable ContainerRequestContext containerRequestContext) {
        log.debug("Checking authority for :: {}",jwtWrap);
        return jwtWrap.hasRole(requiredRole);
    }

}

