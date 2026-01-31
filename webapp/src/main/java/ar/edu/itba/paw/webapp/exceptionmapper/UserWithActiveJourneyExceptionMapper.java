package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.UserWithActiveJourneyException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserWithActiveJourneyExceptionMapper implements ExceptionMapper<UserWithActiveJourneyException> {

    @Override
    public Response toResponse(UserWithActiveJourneyException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.CONFLICT, exception.getMessage()))
                .build();
    }
}
