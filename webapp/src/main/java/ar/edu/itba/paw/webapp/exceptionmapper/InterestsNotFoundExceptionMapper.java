package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InterestsNotFoundExceptionMapper implements ExceptionMapper<InterestsNotFoundException> {

    @Override
    public Response toResponse(InterestsNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
