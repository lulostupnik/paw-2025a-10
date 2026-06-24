package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        final int status = exception.getResponse().getStatus();
        final Response.Status statusEnum = Response.Status.fromStatusCode(status);
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(
                        statusEnum != null ? statusEnum : Response.Status.INTERNAL_SERVER_ERROR,
                        exception.getMessage()))
                .build();
    }
}
