package com.shadow2y.luthen.service.resource;


import com.codahale.metrics.annotation.Timed;
import com.shadow2y.luthen.api.contracts.SignupInitRequest;
import com.shadow2y.luthen.api.contracts.SignupInitResponse;
import com.shadow2y.luthen.api.contracts.SignupVerifyRequest;
import com.shadow2y.luthen.api.contracts.SignupVerifyResponse;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.service.AuthService;
import com.shadow2y.luthen.service.service.IdentityService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/identity")
@Tag(name = "Identity")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdentityResource {

    AuthService authService;
    IdentityService identityService;

    private final Logger log = LoggerFactory.getLogger(IdentityResource.class);

    @Inject
    public IdentityResource(AuthService authService, IdentityService identityService) {
        this.authService = authService;
        this.identityService = identityService;
    }

    @POST
    @Timed
    @UnitOfWork
    @Path("/signup/init")
    public SignupInitResponse initSignup(SignupInitRequest request) throws LuthenError {
        return identityService.initiateSignUp(request);
    }

    @POST
    @Timed
    @UnitOfWork
    @Path("/signup/verify")
    public SignupVerifyResponse verifySignup(SignupVerifyRequest request) throws LuthenError {
        return identityService.verifySignup(request);
    }

}
