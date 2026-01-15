package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;
import ar.edu.itba.paw.webapp.dto.InterestDto;
import ar.edu.itba.paw.webapp.form.CreateInterestForm;
import ar.edu.itba.paw.webapp.form.UpdateInterestForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Path("interests")
@Component
public class InterestController {

    @Autowired
    private InterestService interestService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listInterests(
            // @QueryParam("user") Long userId,
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        // TODO: parametro de usuarios? ¿Acá o en /users/{id}/interests? ¿en webapp o en capa de servicios?
        final List<Interest> interests = interestService.findInterests(search, new PageParams(page + 1, size)).getContent();
        final List<InterestDto> interestDtos = InterestDto.fromInterestCollection(uriInfo, interests);
        return Response.ok(new GenericEntity<>(interestDtos) {}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterestById(@PathParam("id") final long id) {
        final Interest interest = interestService.findInterestById(id).orElseThrow(() -> new InterestsNotFoundException(id));
        return Response.ok(InterestDto.fromInterest(uriInfo, interest)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createInterest(@Valid final CreateInterestForm form) {
        final Interest interest = interestService.createInterest(form.getName());
        return Response.created(UriUtils.getInterestUri(uriInfo, interest.getId()))
                .entity(InterestDto.fromInterest(uriInfo, interest))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateInterest(
            @PathParam("id") final long id,
            @Valid final UpdateInterestForm form
    ) {
        final Interest interest = interestService.updateInterest(id, form.getName());
        return Response.ok(InterestDto.fromInterest(uriInfo, interest)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteInterest(@PathParam("id") final long id) {
        interestService.deleteInterest(id);
        return Response.noContent().build();
    }
}
