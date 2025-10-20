package com.shadow2y.luthen.service.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class LuthenExceptionMapper implements ExceptionMapper<LuthenError> {

    @Override
    public Response toResponse(LuthenError exception) {
        return Response
                .status(exception.error.errorMessage.getCode())
                .entity(exception.error.errorMessage)
                .build();
    }

}
