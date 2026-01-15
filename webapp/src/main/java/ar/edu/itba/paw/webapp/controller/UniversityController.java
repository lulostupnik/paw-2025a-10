package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.exceptions.UniversityNotFoundException;
import ar.edu.itba.paw.webapp.dto.UniversityDto;
import ar.edu.itba.paw.webapp.form.CreateUniversityForm;
import ar.edu.itba.paw.webapp.form.UpdateUniversityForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Path("universities")
@Component
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUniversities(
            // @QueryParam("city") Long cityId, --> por lo menos por ahora no
            // @QueryParam("country") Long countryId, --> idem
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final List<University> universities = universityService.findUniversities(search, new PageParams(page + 1, size)).getContent();
        final List<UniversityDto> universityDtos = UniversityDto.fromUniversityCollection(uriInfo, universities);
        return Response.ok(new GenericEntity<>(universityDtos) {}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUniversityById(@PathParam("id") final long id) {
        final University university = universityService.findById(id).orElseThrow(() -> new UniversityNotFoundException(id));
        return Response.ok(UniversityDto.fromUniversity(uriInfo, university)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUniversity(@Valid final CreateUniversityForm form) {
        final University university = universityService.createUniversity(form.getName(), form.getAbbreviation(), form.getCity());
        return Response.created(UriUtils.getUniversityUri(uriInfo, university.getId()))
                .entity(UniversityDto.fromUniversity(uriInfo, university))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUniversity(
            @PathParam("id") final long id,
            @Valid final UpdateUniversityForm form
    ) {
        final University university = universityService.updateUniversity(id, form.getName(), form.getAbbreviation(), form.getCity());
        return Response.ok(UniversityDto.fromUniversity(uriInfo, university)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUniversity(@PathParam("id") final long id) {
        universityService.deleteUniversity(id);
        return Response.noContent().build();
    }
}
