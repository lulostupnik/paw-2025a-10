package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Country;
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
public class CountryDto {

    private long id;
    private String name;
    private String code;

    private Links links;

    public static CountryDto fromCountry(final UriInfo uriInfo, final Country country) {
        final CountryDto dto = new CountryDto();
        dto.id = country.getId();
        dto.name = country.getName();
        dto.code = country.getCode();
        final Links links = new Links();
        links.selfUrl = UriUtils.getCountryUri(uriInfo, country.getId());
        dto.links = links;
        return dto;
    }

    public static List<CountryDto> fromCountryCollection(final UriInfo uriInfo, final Collection<Country> countries) {
        return countries.stream().map(country -> fromCountry(uriInfo, country)).toList();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;

        public URI getSelfUrl() { return selfUrl; }
    }
}
