package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.springframework.dao.DataIntegrityViolationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DataIntegrityViolationExceptionMapper implements ExceptionMapper<DataIntegrityViolationException> {

    @Override
    public Response toResponse(DataIntegrityViolationException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.CONFLICT,
                        "The request conflicts with the current state of the resource."))
                .build();
    }
}
