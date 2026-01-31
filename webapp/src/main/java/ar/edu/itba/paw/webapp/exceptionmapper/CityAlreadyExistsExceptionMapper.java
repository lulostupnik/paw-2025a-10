package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.CityAlreadyExistsException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CityAlreadyExistsExceptionMapper implements ExceptionMapper<CityAlreadyExistsException> {

    @Override
    public Response toResponse(CityAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.CONFLICT, exception.getMessage()))
                .build();
    }
}
