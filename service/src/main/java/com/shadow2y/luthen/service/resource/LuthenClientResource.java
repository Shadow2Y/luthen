package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.model.luthenclient.ClientRefreshRequest;
import com.shadow2y.luthen.service.model.luthenclient.ClientRefreshResponse;
import com.shadow2y.luthen.service.service.LuthenClientService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/client")
@Tag(name = "Luthen Client")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LuthenClientResource {

    private final Logger log = LoggerFactory.getLogger(LuthenClientResource.class);

    LuthenClientService service;

    @Inject
    public LuthenClientResource(LuthenClientService service) {
        this.service = service;
    }

    @POST
    @UnitOfWork
    @Path("/refresh")
    public ClientRefreshResponse refreshRoles(@HeaderParam("filterKey") String filterKey, ClientRefreshRequest request) throws LuthenError {
        log.info("Received Client Refresh request");
        return this.service.getAuthData(filterKey,request);
    }

}
