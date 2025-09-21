package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.api.models.AuthResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.jetty.security.authentication.AuthorizationService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    AuthorizationService authorizationService;

    public AuthResource(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @POST
    @Path("/validate")
    public AuthResponse validateAuth() {
        return null;
    }

}
