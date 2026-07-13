package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.models.exceptions.BusinessException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(final BusinessException exception) {
        final Response.Status status = Response.Status.fromStatusCode(exception.getStatus());
        final String message = messageSource.getMessage(exception.getMessageKey(), null,
                exception.getMessageKey(), LocaleContextHolder.getLocale());
        LOGGER.warn("BusinessException {} -> {} ({})", exception.getClass().getSimpleName(), exception.getMessageKey(), status);
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(status, message))
                .build();
    }
}
