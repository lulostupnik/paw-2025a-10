package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CityDto {

    private long id;
    private String name;
    private String country;

    private Links links;

    public static CityDto fromCity(final UriInfo uriInfo, final City city) {
        final CityDto dto = new CityDto();
        dto.id = city.getId();
        dto.name = city.getName();
        dto.country = city.getCountry().getName();
        final Links links = new Links();
        links.selfUrl = UriUtils.getCityUri(uriInfo, city.getId());
        links.countryUrl = UriUtils.getCountryUri(uriInfo, city.getCountry().getId());
        dto.links = links;
        return dto;
    }

    public static List<CityDto> fromCityCollection(final UriInfo uriInfo, final Collection<City> cities) {
        return cities.stream().map(city -> fromCity(uriInfo, city)).toList();
    }


    public long getId() { return id; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI countryUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getCountryUrl() { return countryUrl; }
    }
}
