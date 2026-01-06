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

    private URI selfUrl;

    public static CountryDto fromCountry(final UriInfo uriInfo, final Country country) {
        final CountryDto dto = new CountryDto();
        dto.id = country.getId();
        dto.name = country.getName();
        dto.code = country.getCode();
        dto.selfUrl = UriUtils.getCountryUri(uriInfo, country.getId());
        return dto;
    }

    public static List<CountryDto> fromCountryCollection(final UriInfo uriInfo, final Collection<Country> countries) {
        return countries.stream().map(country -> fromCountry(uriInfo, country)).toList();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public URI getSelfUrl() { return selfUrl; }
}
