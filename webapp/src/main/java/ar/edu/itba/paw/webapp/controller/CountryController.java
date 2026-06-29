package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.exceptions.CountryNotFoundException;
import ar.edu.itba.paw.webapp.CustomMediaType;
import ar.edu.itba.paw.webapp.dto.CountryDto;
import ar.edu.itba.paw.webapp.utils.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.List;

@Path("countries")
@Component
public class CountryController {

    @Autowired
    private CountryService countryService;

    @Context
    private UriInfo uriInfo;

    // Lista acotada no mutable sin paginacion 
    @GET
    @Produces(CustomMediaType.APPLICATION_COUNTRY_LIST)
    public Response listCountries(@Context Request req) {
        final List<Country> countries = countryService.findCountries();
        final List<CountryDto> countryDtos = CountryDto.fromCountryCollection(uriInfo, countries);
        final ResponseBuilder response = Response.ok(new GenericEntity<>(countryDtos) {});
        return CacheUtils.withEtag(req, countries, response);
    }

    @GET
    @Path("/{id}")
    @Produces(CustomMediaType.APPLICATION_COUNTRY)
    public Response getCountryById(@Context Request req, @PathParam("id") final long id) {
        final Country country = countryService.findCountryById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country not found", String.valueOf(id)));
        return CacheUtils.withEtag(req, country, () -> CountryDto.fromCountry(uriInfo, country));
    }
}
