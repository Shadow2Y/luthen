package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.api.models.auth.AuthResponse;
import com.shadow2y.luthen.api.models.auth.LoginRequest;
import com.shadow2y.luthen.api.models.auth.SignupRequest;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    @Path("/signup")
    public String signup(SignupRequest request) throws LuthenError {
        authService.signUp(request);
        return "NICE";
    }

    @POST
    @Path("/login")
    public String login(LoginRequest request) throws LuthenError {
        authService.login(request);
        return "NICE";
    }

    @POST
    @Path("/refresh")
    public AuthResponse refresh() {
        return null;
    }

}
