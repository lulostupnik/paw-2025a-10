package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.exceptions.UniversityNotFoundException;
import ar.edu.itba.paw.webapp.GoTogetherMediaType;
import ar.edu.itba.paw.webapp.dto.UniversityDto;
import ar.edu.itba.paw.webapp.form.CreateUniversityForm;
import ar.edu.itba.paw.webapp.form.PatchUniversityForm;
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

@Path("universities")
@Component
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(GoTogetherMediaType.APPLICATION_UNIVERSITY_LIST)
    public Response listUniversities(
            @Context Request req,
            // @QueryParam("city") Long cityId, --> por lo menos por ahora no
            // @QueryParam("country") Long countryId, --> idem
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final Page<University> universities = universityService.findUniversities(search, new PageParams(page, size));
        final List<UniversityDto> universityDtos = UniversityDto.fromUniversityCollection(uriInfo, universities.getContent());
        final ResponseBuilder response = PagingUtils.insertPaginationLinks(
                Response.ok(new GenericEntity<>(universityDtos) {}), uriInfo, universities);
        return CacheUtils.withEtag(req, universities.getContent(), response);
    }

    @GET
    @Path("/{id}")
    @Produces(GoTogetherMediaType.APPLICATION_UNIVERSITY)
    public Response getUniversityById(@Context Request req, @PathParam("id") final long id) {
        final University university = universityService.findById(id).orElseThrow(() -> new UniversityNotFoundException());
        return CacheUtils.withEtag(req, university, () -> UniversityDto.fromUniversity(uriInfo, university));
    }

    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_UNIVERSITY)
    @Produces(GoTogetherMediaType.APPLICATION_UNIVERSITY)
    public Response createUniversity(@Valid @NotNull final CreateUniversityForm form) {
        final University university = universityService.createUniversity(form.getName(), form.getAbbreviation(), form.getCityId());
        return Response.created(UriUtils.getUniversityUri(uriInfo, university.getId()))
                .entity(UniversityDto.fromUniversity(uriInfo, university))
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_UNIVERSITY)
    @Produces(GoTogetherMediaType.APPLICATION_UNIVERSITY)
    public Response patchUniversity(
            @PathParam("id") final long id,
            @Valid @NotNull final PatchUniversityForm form
    ) {
        final University university = universityService.patchUniversity(id, form.getName(), form.getAbbreviation(), form.getCityId());
        return Response.ok(UniversityDto.fromUniversity(uriInfo, university)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUniversity(@PathParam("id") final long id) {
        universityService.deleteUniversity(id);
        return Response.noContent().build();
    }
}
