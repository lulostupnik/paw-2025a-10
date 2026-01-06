package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.CityNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CityNotFoundExceptionMapper implements ExceptionMapper<CityNotFoundException> {

    @Override
    public Response toResponse(CityNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
