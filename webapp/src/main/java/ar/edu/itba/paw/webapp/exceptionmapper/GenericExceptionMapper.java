package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.error("Unhandled exception", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.INTERNAL_SERVER_ERROR, "Internal server error"))
                .build();
    }
}
