package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.service.service.EntityService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/entity")
@Produces(MediaType.APPLICATION_JSON)
public class EntityResource {

    private final EntityService entityService;

    @Inject
    public EntityResource(EntityService entityService) {
        this.entityService = entityService;
    }

    @GET
    @Path("/hello")
    public String hello() {
        System.out.println("CAME HERE");
        return "Hello from Luthen";
    }

}
