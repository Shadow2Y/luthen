package com.shadow2y.luthen.service.resource;

import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.model.luthenclient.ClientRefreshRequest;
import com.shadow2y.luthen.service.model.luthenclient.ClientRefreshResponse;
import com.shadow2y.luthen.service.service.LuthenClientService;
import com.shadow2y.luthen.service.utils.CryptoUtils;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/client")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LuthenClientResource {

    private final Logger log = LoggerFactory.getLogger(LuthenClientResource.class);

    LuthenClientService service;

    public LuthenClientResource(LuthenClientService service) {
        this.service = service;
    }

    @POST
    @UnitOfWork
    @Path("/refresh")
    public ClientRefreshResponse refreshRoles(@HeaderParam("filterKey") String filterKey, ClientRefreshRequest request) throws LuthenError {
        if(CryptoUtils.verify(filterKey)) {
            log.error("Client Filter verification failed");
            throw new LuthenError(Error.CLIENT_VALIDATION_FAILED);
        } else {
            return this.service.getAuthData(request);
        }
    }

}
