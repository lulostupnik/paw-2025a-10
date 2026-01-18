package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;
import ar.edu.itba.paw.webapp.dto.CareerDto;
import ar.edu.itba.paw.webapp.form.CreateCareerForm;
import ar.edu.itba.paw.webapp.form.PatchCareerForm;
import ar.edu.itba.paw.webapp.form.UpdateCareerForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Path("careers")
@Component
public class CareerController {

    @Autowired
    private CareerService careerService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCareers(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final List<Career> careers = careerService.searchCareers(search, new PageParams(page + 1, size)).getContent();
        final List<CareerDto> careerDtos = CareerDto.fromCareerCollection(uriInfo, careers);
        return Response.ok(new GenericEntity<>(careerDtos) {}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCareerById(@PathParam("id") final long id) {
        final Career career = careerService.findCareerById(id).orElseThrow(() -> new CareerNotFoundException(id));
        return Response.ok(CareerDto.fromCareer(uriInfo, career)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCareer(@Valid final CreateCareerForm form) {
        final Career career = careerService.createCareer(form.getName());
        return Response.created(UriUtils.getCareerUri(uriInfo, career.getId()))
                .entity(CareerDto.fromCareer(uriInfo, career))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCareer(
            @PathParam("id") final long id,
            @Valid final UpdateCareerForm form
    ) {
        final Career career = careerService.updateCareer(id, form.getName());
        return Response.ok(CareerDto.fromCareer(uriInfo, career)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchCareer(
            @PathParam("id") final long id,
            @Valid final PatchCareerForm form
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
