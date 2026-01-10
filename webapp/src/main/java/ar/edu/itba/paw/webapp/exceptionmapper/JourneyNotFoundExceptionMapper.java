package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JourneyNotFoundExceptionMapper implements ExceptionMapper<JourneyNotFoundException> {

    @Override
    public Response toResponse(JourneyNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}
