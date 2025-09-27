package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.api.models.auth.AuthResponse;
import com.shadow2y.luthen.api.models.auth.LoginRequest;
import com.shadow2y.luthen.api.models.auth.LoginResponse;
import com.shadow2y.luthen.service.service.AuthService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jetty.security.authentication.AuthorizationService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/login")
    public String login(LoginRequest request) {
        var sessionToken = authService.login(request);
        return null;
    }

    @POST
    @Path("/refresh")
    public AuthResponse refresh() {
        return null;
    }

}
