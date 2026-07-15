package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final String MESSAGE_KEY_PREFIX = "error.http.";

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(WebApplicationException exception) {
        final Response original = exception.getResponse();
        final int status = original.getStatus();
        final Response.Status statusEnum = Response.Status.fromStatusCode(status);
        final String message = messageSource.getMessage(MESSAGE_KEY_PREFIX + status, null,
                exception.getMessage(), LocaleContextHolder.getLocale());
        final Response.ResponseBuilder builder = Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(
                        statusEnum != null ? statusEnum : Response.Status.INTERNAL_SERVER_ERROR,
                        message));
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
