package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.CityNotFoundException;
import ar.edu.itba.paw.webapp.GoTogetherMediaType;
import ar.edu.itba.paw.webapp.dto.CityDto;
import ar.edu.itba.paw.webapp.form.CreateCityForm;
import ar.edu.itba.paw.webapp.form.PatchCityForm;
import ar.edu.itba.paw.webapp.form.UpdateCityForm;
import ar.edu.itba.paw.webapp.utils.CacheUtils;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.List;

@Path("cities")
@Component
public class CityController {

    @Autowired
    private CityService cityService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(GoTogetherMediaType.APPLICATION_CITY_LIST)
    public Response listCities(
            @Context Request req,
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final Page<City> cities = cityService.searchCities(search, new PageParams(page, size));
        final List<CityDto> cityDtos = CityDto.fromCityCollection(uriInfo, cities.getContent());
        final ResponseBuilder response = PagingUtils.insertPaginationLinks(
                Response.ok(new GenericEntity<>(cityDtos) {}), uriInfo, cities);
        return CacheUtils.withEtag(req, cities.getContent(), response);
    }

    @GET
    @Path("/{id}")
    @Produces(GoTogetherMediaType.APPLICATION_CITY)
    public Response getCityById(@Context Request req, @PathParam("id") final long id) {
        final City city = cityService.findCityById(id).orElseThrow(() -> new CityNotFoundException(id));
        return CacheUtils.withEtag(req, city, () -> CityDto.fromCity(uriInfo, city));
    }

    @POST
    @Consumes(GoTogetherMediaType.APPLICATION_CITY)
    @Produces(GoTogetherMediaType.APPLICATION_CITY)
    public Response createCity(@Valid final CreateCityForm form) {
        final City city = cityService.createCity(form.getName(), form.getCountryId());
        return Response.created(UriUtils.getCityUri(uriInfo, city.getId()))
                .entity(CityDto.fromCity(uriInfo, city))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_CITY)
    @Produces(GoTogetherMediaType.APPLICATION_CITY)
    public Response updateCity(
            @PathParam("id") final long id,
            @Valid final UpdateCityForm form
    ) {
        final City city = cityService.updateCity(id, form.getName(), form.getCountryId());
        return Response.ok(CityDto.fromCity(uriInfo, city)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(GoTogetherMediaType.APPLICATION_CITY)
    @Produces(GoTogetherMediaType.APPLICATION_CITY)
    public Response patchCity(
            @PathParam("id") final long id,
            @Valid final PatchCityForm form
    ) {
        final City city = cityService.patchCity(id, form.getName(), form.getCountryId());
        return Response.ok(CityDto.fromCity(uriInfo, city)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCity(@PathParam("id") final long id) {
        cityService.deleteCity(id);
        return Response.noContent().build();
    }
}
