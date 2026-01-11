package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.JourneyResponseNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JourneyResponseNotFoundExceptionMapper implements ExceptionMapper<JourneyResponseNotFoundException> {

    @Override
    public Response toResponse(JourneyResponseNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}
