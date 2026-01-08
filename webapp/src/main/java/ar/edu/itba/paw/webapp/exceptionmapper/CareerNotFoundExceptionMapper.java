package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CareerNotFoundExceptionMapper implements ExceptionMapper<CareerNotFoundException> {

    @Override
    public Response toResponse(CareerNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
