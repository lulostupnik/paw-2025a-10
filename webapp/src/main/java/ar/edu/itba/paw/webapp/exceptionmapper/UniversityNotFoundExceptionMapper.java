package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.UniversityNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UniversityNotFoundExceptionMapper implements ExceptionMapper<UniversityNotFoundException> {

    @Override
    public Response toResponse(UniversityNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
