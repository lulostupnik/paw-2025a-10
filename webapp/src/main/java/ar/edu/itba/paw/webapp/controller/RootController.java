package ar.edu.itba.paw.webapp.controller;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

//@TODO Creo que esta mal. hay que poner los URLS.
@Path("/")
@Component
public class RootController {
    @HEAD
    @javax.ws.rs.Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    public Response head() {
        return Response.ok().header("X-Service", "webapp").build();
    }
}
