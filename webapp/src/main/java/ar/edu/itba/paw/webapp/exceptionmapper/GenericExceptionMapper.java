package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDto.fromException(Response.Status.NOT_FOUND, exception.getMessage()))
                    .build();
        }
        if (exception instanceof NotAllowedException) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDto.fromException(Response.Status.METHOD_NOT_ALLOWED, exception.getMessage()))
                    .build();
        }
        if (exception instanceof NotSupportedException) {
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDto.fromException(Response.Status.UNSUPPORTED_MEDIA_TYPE, exception.getMessage()))
                    .build();
        }
        LOGGER.error("Unhandled exception", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage()))
                .build();
    }
}
