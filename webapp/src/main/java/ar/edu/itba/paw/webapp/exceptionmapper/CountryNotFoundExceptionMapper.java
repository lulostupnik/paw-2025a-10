package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.CountryNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CountryNotFoundExceptionMapper implements ExceptionMapper<CountryNotFoundException> {

    @Override
    public Response toResponse(CountryNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
