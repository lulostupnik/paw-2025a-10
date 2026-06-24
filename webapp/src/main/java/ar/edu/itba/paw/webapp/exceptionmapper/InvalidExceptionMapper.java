package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.InvalidException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidExceptionMapper implements ExceptionMapper<InvalidException> {

    @Override
    public Response toResponse(InvalidException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.BAD_REQUEST, exception.getMessage()))
                .build();
    }
}
