package ar.edu.itba.paw.webapp.controller;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

@Path("/")
@Component
public class RootController {

    @HEAD
    @Produces(MediaType.TEXT_PLAIN)
    public Response head() {
        return Response.noContent().header("X-Service", "webapp").build();
    }
}
