package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.BusinessException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Provider
@Component
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessExceptionMapper.class);
    private static final Locale DEFAULT_LOCALE = new Locale("en");

    @Autowired
    private MessageSource messageSource;

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(final BusinessException exception) {
        final Response.Status status = Response.Status.fromStatusCode(exception.getStatus());
        final String message = localize(exception.getMessageKey());
        LOGGER.warn("BusinessException {} -> {} ({})", exception.getClass().getSimpleName(), exception.getMessageKey(), status);
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(status, message))
                .build();
    }

    private String localize(final String key) {
        try {
            return messageSource.getMessage(key, null, resolveLocale());
        } catch (Exception e) {
            return key;
        }
    }

    private Locale resolveLocale() {
        if (headers != null) {
            for (final Locale locale : headers.getAcceptableLanguages()) {
                final String lang = locale.getLanguage().toLowerCase();
                if (lang.equals("es") || lang.equals("en")) {
                    return locale;
                }
            }
        }
        return DEFAULT_LOCALE;
    }
}
