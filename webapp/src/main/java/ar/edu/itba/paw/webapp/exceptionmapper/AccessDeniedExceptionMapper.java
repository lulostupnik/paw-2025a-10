package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.auth.AuthUtils;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
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
    private static final String AUTH_SCHEMES = "Basic realm=\"GoTogether\", Bearer realm=\"GoTogether\"";

    @Override
    public Response toResponse(AccessDeniedException ex) {
        final boolean isAnonymous = AuthUtils.getCurrentUserId() == null;

        if (isAnonymous) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .header(AUTH_HEADER, AUTH_SCHEMES)
                    .entity(ErrorDto.fromException(Response.Status.UNAUTHORIZED, ex.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.FORBIDDEN, ex.getMessage()))
                .build();
    }
}
