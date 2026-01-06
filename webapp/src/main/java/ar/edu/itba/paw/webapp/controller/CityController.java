package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.webapp.dto.CityDto;
import ar.edu.itba.paw.webapp.form.CreateCityForm;
import ar.edu.itba.paw.webapp.form.UpdateCityForm;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Path("cities")
@Component
public class CityController {

    @Autowired
    private CityService cityService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCities(
            // @QueryParam("country") Long countryId,
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        final List<City> cities = cityService.searchCities(search, new PageParams(page + 1, size)).getContent();
        final List<CityDto> cityDtos = CityDto.fromCityCollection(uriInfo, cities);
        return Response.ok(new GenericEntity<>(cityDtos) {}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCityById(@PathParam("id") final long id) {
        final Optional<City> maybeCity = cityService.findCityById(id);
        if (maybeCity.isPresent()) {
            return Response.ok(CityDto.fromCity(uriInfo, maybeCity.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCity(@Valid final CreateCityForm form) {
        final City city = cityService.createCity(form.getName(), form.getCountry());
        return Response.created(UriUtils.getCityUri(uriInfo, city.getId())).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCity(
            @PathParam("id") final long id,
            @Valid final UpdateCityForm form
    ) {
        final City city = cityService.updateCity(id, form.getName(), form.getCountry());
        return Response.ok(CityDto.fromCity(uriInfo, city)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCity(@PathParam("id") final long id) {
        cityService.deleteCity(id);
        return Response.noContent().build();
    }
}
