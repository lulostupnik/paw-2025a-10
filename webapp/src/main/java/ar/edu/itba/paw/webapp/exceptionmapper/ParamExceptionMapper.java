package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.glassfish.jersey.server.ParamException;
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
public class ParamExceptionMapper implements ExceptionMapper<ParamException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(ParamException exception) {
        final String message = messageSource.getMessage("error.invalidParam",
                new Object[]{exception.getParameterName()},
                "Invalid parameter: " + exception.getParameterName(),
                LocaleContextHolder.getLocale());
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.BAD_REQUEST, message))
                .build();
    }
}
