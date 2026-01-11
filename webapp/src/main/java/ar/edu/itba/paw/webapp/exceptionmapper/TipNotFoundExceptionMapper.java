package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.TipNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TipNotFoundExceptionMapper implements ExceptionMapper<TipNotFoundException> {

    @Override
    public Response toResponse(TipNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}
