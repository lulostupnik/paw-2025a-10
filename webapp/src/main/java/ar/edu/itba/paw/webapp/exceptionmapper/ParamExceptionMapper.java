package ar.edu.itba.paw.webapp.exceptionmapper;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import org.glassfish.jersey.server.ParamException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParamExceptionMapper implements ExceptionMapper<ParamException> {

    @Override
    public Response toResponse(ParamException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorDto.fromException(Response.Status.BAD_REQUEST,
                        "Invalid parameter: " + exception.getParameterName()))
                .build();
    }
}
