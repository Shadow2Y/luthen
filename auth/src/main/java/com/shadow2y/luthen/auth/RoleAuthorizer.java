package com.shadow2y.luthen.auth;

import com.shadow2y.luthen.api.response.UserAuth;
import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RoleAuthorizer implements Authorizer<UserAuth> {

    @Override
    public boolean authorize(UserAuth user, String requiredRole, @Nullable ContainerRequestContext containerRequestContext) {
        return user.getRoles().contains(requiredRole);
    }

}

