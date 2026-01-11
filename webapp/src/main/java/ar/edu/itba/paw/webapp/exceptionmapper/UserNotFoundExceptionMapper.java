package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.UserNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {

    @Override
    public Response toResponse(UserNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}
