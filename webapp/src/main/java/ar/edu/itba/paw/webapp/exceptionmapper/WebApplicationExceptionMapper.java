package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        final Response original = exception.getResponse();
        final int status = original.getStatus();
        final Response.Status statusEnum = Response.Status.fromStatusCode(status);
        final Response.ResponseBuilder builder = Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(
                        statusEnum != null ? statusEnum : Response.Status.INTERNAL_SERVER_ERROR,
                        exception.getMessage()));
        copyHeaderIfPresent(builder, original, HttpHeaders.ALLOW);
        copyHeaderIfPresent(builder, original, HttpHeaders.WWW_AUTHENTICATE);
        return builder.build();
    }

    private void copyHeaderIfPresent(final Response.ResponseBuilder builder, final Response original, final String header) {
        final String value = original.getHeaderString(header);
        if (value != null) {
            builder.header(header, value);
        }
    }
}
