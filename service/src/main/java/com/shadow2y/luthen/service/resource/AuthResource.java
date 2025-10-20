package com.shadow2y.luthen.service.resource;

import com.codahale.metrics.annotation.Timed;
import com.nimbusds.jwt.JWTClaimsSet;
import com.shadow2y.luthen.api.contracts.*;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.service.AuthService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@Path("/auth")
@Tag(name = "Authentication")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    AuthService authService;

    private final Logger log = LoggerFactory.getLogger(AuthResource.class);

    @Inject
    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Timed
    @UnitOfWork
    @Path("/login")
    public LoginResponse login(LoginRequest request) throws LuthenError {
        log.info("Received `/login` request");
        var response = authService.login(request);
        log.info("Responding to `/login` request");
        return response;
    }

    @POST
    @UnitOfWork
    @RolesAllowed("test")
    @Path("/create/role")
    public Response createRole(CreateRoleRequest request) throws LuthenError {
        var response = authService.getOrCreateRole(request);
        return jakarta.ws.rs.core.Response.ok()
                .entity(response)
                .build();
    }

    @POST
    @UnitOfWork
    @RolesAllowed("test")
    @Path("/create/permission")
    public Response createPermission(CreatePermissionRequest request) throws LuthenError {
        var response = authService.getOrCreatePermission(request.name(), request.description());
        return jakarta.ws.rs.core.Response.ok()
                .entity(response)
                .build();
    }

    @POST
    @UnitOfWork
    @Path("/introspect")
    public Map<String, Object> introspect(String token) throws LuthenError {
        return authService.introspect(token);
    }

}
