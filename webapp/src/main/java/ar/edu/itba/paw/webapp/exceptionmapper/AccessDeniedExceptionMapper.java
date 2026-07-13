package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.auth.AuthUtils;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    private static final String AUTH_HEADER = "WWW-Authenticate";
    private static final String AUTH_SCHEMES = "Bearer realm=\"GoTogether\"";

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(AccessDeniedException ex) {
        final boolean isAnonymous = AuthUtils.getCurrentUserId() == null;

        if (isAnonymous) {
            final String message = messageSource.getMessage("error.unauthorized", null,
                    "Authentication is required to access this resource.", LocaleContextHolder.getLocale());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .header(AUTH_HEADER, AUTH_SCHEMES)
                    .entity(ErrorDto.fromException(Response.Status.UNAUTHORIZED, message))
                    .build();
        }

        final String message = messageSource.getMessage("error.accessDenied", null,
                "You don't have permission to access this resource.", LocaleContextHolder.getLocale());
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.FORBIDDEN, message))
                .build();
    }
}
