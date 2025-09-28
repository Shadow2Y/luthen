package com.shadow2y.luthen.service.exception.mapper;

import com.shadow2y.luthen.service.exception.LuthenError;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class LuthenExceptionMapper implements ExceptionMapper<LuthenError> {

    @Override
    public Response toResponse(LuthenError exception) {
        return Response
                .status(exception.error.getStatusCode())
                .entity(exception.error.getErrorMessage())
                .build();
    }

}
