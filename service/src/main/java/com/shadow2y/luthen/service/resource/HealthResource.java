package com.shadow2y.luthen.service.resource;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Singleton
@Path("/health")
@Tag(name = "Health")
public class HealthResource {

    @GET
    @Path("/check")
    public String check() {
        return "OK";
    }

}
