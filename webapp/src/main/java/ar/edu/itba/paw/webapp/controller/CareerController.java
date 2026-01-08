package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.webapp.dto.CareerDto;
import ar.edu.itba.paw.webapp.form.CreateCareerForm;
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
        final Optional<Career> maybeCareer = careerService.findCareerById(id);
        if (maybeCareer.isPresent()) {
            return Response.ok(CareerDto.fromCareer(uriInfo, maybeCareer.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCareer(@Valid final CreateCareerForm form) {
        final Career career = careerService.createCareer(form.getName());
        return Response.created(UriUtils.getCareerUri(uriInfo, career.getId())).build();
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

    @DELETE
    @Path("/{id}")
    public Response deleteCareer(@PathParam("id") final long id) {
        careerService.deleteCareer(id);
        return Response.noContent().build();
    }
}
