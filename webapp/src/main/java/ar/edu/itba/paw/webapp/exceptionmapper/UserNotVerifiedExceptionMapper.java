package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.UserNotVerifiedException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotVerifiedExceptionMapper implements ExceptionMapper<UserNotVerifiedException> {

    @Override
    public Response toResponse(UserNotVerifiedException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.FORBIDDEN, exception.getMessage()))
                .build();
    }
}
