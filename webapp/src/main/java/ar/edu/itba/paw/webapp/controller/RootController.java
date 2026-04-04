package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.RootIndexDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
@Component
public class RootController {

    @Context
    private UriInfo uriInfo;

    @GET
    public Response homeIndex() {
        return Response.ok(RootIndexDto.fromUriInfo(uriInfo)).build();
    }

    @HEAD
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    public Response head() {
        return Response.ok().header("X-Service", "webapp").build();
    }
}
