package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;
import ar.edu.itba.paw.webapp.GoTogetherMediaType;
import ar.edu.itba.paw.webapp.dto.CareerDto;
import ar.edu.itba.paw.webapp.form.CreateCareerForm;
import ar.edu.itba.paw.webapp.form.PatchCareerForm;
import ar.edu.itba.paw.webapp.form.UpdateCareerForm;
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

@Path("careers")
@Component
public class CareerController {

    @Autowired
    private CareerService careerService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(GoTogetherMediaType.APPLICATION_CAREER_LIST)
    public Response listCareers(
            @Context Request req,
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final Page<Career> careers = careerService.searchCareers(search, new PageParams(page, size));
        final List<CareerDto> careerDtos = CareerDto.fromCareerCollection(uriInfo, careers.getContent());
        final ResponseBuilder response = PagingUtils.insertPaginationLinks(
                Response.ok(new GenericEntity<>(careerDtos) {}), uriInfo, careers);
        return CacheUtils.withEtag(req, careers.getContent(), response);
    }

    @GET
    @Path("/{id}")
    @Produces(GoTogetherMediaType.APPLICATION_CAREER)
    public Response getCareerById(@Context Request req, @PathParam("id") final long id) {
        final Career career = careerService.findCareerById(id).orElseThrow(() -> new CareerNotFoundException(id));
        return CacheUtils.withEtag(req, career, () -> CareerDto.fromCareer(uriInfo, career));
    }

    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_CAREER)
    @Produces(GoTogetherMediaType.APPLICATION_CAREER)
    public Response createCareer(@Valid @NotNull final CreateCareerForm form) {
        final Career career = careerService.createCareer(form.getName());
        return Response.created(UriUtils.getCareerUri(uriInfo, career.getId()))
                .entity(CareerDto.fromCareer(uriInfo, career))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_CAREER)
    @Produces(GoTogetherMediaType.APPLICATION_CAREER)
    public Response updateCareer(
            @PathParam("id") final long id,
            @Valid @NotNull final UpdateCareerForm form
    ) {
        final Career career = careerService.updateCareer(id, form.getName());
        return Response.ok(CareerDto.fromCareer(uriInfo, career)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_CAREER)
    @Produces(GoTogetherMediaType.APPLICATION_CAREER)
    public Response patchCareer(
            @PathParam("id") final long id,
            @Valid @NotNull final PatchCareerForm form
    ) {
        final Career career = careerService.patchCareer(id, form.getName());
        return Response.ok(CareerDto.fromCareer(uriInfo, career)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCareer(@PathParam("id") final long id) {
        careerService.deleteCareer(id);
        return Response.noContent().build();
    }
}
