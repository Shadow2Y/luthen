package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.auth.models.JWTWrap;
import io.dropwizard.auth.Auth;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Path("/test")
@Tag(name = "TEST")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestResource {

    @GET
    @Path("/token")
    @RolesAllowed("TEST")
    public Response test(@Auth JWTWrap user) {
        return Response.ok(Map.of("user", user)).build();
    }

    @GET
    @Path("/dummy")
    public Response dummy() {
        return Response.ok(Map.of("user", "headers")).build();
    }
}
