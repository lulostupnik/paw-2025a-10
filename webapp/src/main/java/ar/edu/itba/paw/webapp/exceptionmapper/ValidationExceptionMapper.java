package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        LOGGER.debug("Validation error: {}", exception.getConstraintViolations());

        final ErrorDto errorDto = ErrorDto.fromValidationErrors(exception.getConstraintViolations());

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDto)
                .build();
    }
}
