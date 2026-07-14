package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;
import ar.edu.itba.paw.webapp.GoTogetherMediaType;
import ar.edu.itba.paw.webapp.dto.InterestDto;
import ar.edu.itba.paw.webapp.form.CreateInterestForm;
import ar.edu.itba.paw.webapp.form.PatchInterestForm;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.CacheUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.List;

@Path("interests")
@Component
public class InterestController {

    @Autowired
    private InterestService interestService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(GoTogetherMediaType.APPLICATION_INTEREST_LIST)
    public Response listInterests(
            @Context Request req,
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        // TODO: parametro de usuarios? ¿Acá o en /users/{id}/interests? ¿en webapp o en capa de servicios?
        final Page<Interest> interests = interestService.findInterests(search, new PageParams(page, size));
        final List<InterestDto> interestDtos = InterestDto.fromInterestCollection(uriInfo, interests.getContent());
        final ResponseBuilder response = PagingUtils.insertPaginationLinks(
                Response.ok(new GenericEntity<>(interestDtos) {}), uriInfo, interests);
        return CacheUtils.withEtag(req, interests.getContent(), response);
    }

    @GET
    @Path("/{id}")
    @Produces(GoTogetherMediaType.APPLICATION_INTEREST)
    public Response getInterestById(@Context Request req, @PathParam("id") final long id) {
        final Interest interest = interestService.findInterestById(id).orElseThrow(() -> new InterestsNotFoundException(id));
        return CacheUtils.withEtag(req, interest, () -> InterestDto.fromInterest(uriInfo, interest));
    }

    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_INTEREST)
    @Produces(GoTogetherMediaType.APPLICATION_INTEREST)
    public Response createInterest(@Valid @NotNull final CreateInterestForm form) {
        final Interest interest = interestService.createInterest(form.getName());
        return Response.created(UriUtils.getInterestUri(uriInfo, interest.getId()))
                .entity(InterestDto.fromInterest(uriInfo, interest))
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_INTEREST)
    @Produces(GoTogetherMediaType.APPLICATION_INTEREST)
    public Response patchInterest(
            @PathParam("id") final long id,
            @Valid @NotNull final PatchInterestForm form
    ) {
        final Interest interest = interestService.patchInterest(id, form.getName());
        return Response.ok(InterestDto.fromInterest(uriInfo, interest)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteInterest(@PathParam("id") final long id) {
        interestService.deleteInterest(id);
        return Response.noContent().build();
    }
}
