package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.api.request.CreatePermissionRequest;
import com.shadow2y.luthen.api.request.CreateRoleRequest;
import com.shadow2y.luthen.api.request.LoginRequest;
import com.shadow2y.luthen.api.response.LoginResponse;
import com.shadow2y.luthen.api.request.SignupRequest;
import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.service.AuthService;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    AuthService authService;

    @Inject
    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @UnitOfWork
    @Path("/signup")
    public UserSummary signup(SignupRequest request) throws LuthenError {
        return authService.signUp(request);
    }

    @POST
    @UnitOfWork
    @Path("/login")
    public LoginResponse login(LoginRequest request) throws LuthenError {
        return authService.login(request);
    }

    @POST
    @Path("/test")
    @RolesAllowed("test")
    public String test() {
        return "SUCCESS";
    }

    @POST
    @UnitOfWork
    @RolesAllowed("test")
    @Path("/create/role")
    public Response createRole(CreateRoleRequest request) throws LuthenError {
        var response = authService.getOrCreateRole(request);
        return Response.ok()
                .entity(response)
                .build();
    }

    @POST
    @UnitOfWork
    @RolesAllowed("test")
    @Path("/create/permission")
    public Response createPermission(CreatePermissionRequest request) throws LuthenError {
        var response = authService.getOrCreatePermission(request.name, request.description);
        return Response.ok()
                .entity(response)
                .build();
    }

}
