package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.University;
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
public class UniversityDto {

    private long id;
    private String name;
    private String abbreviation;

    private URI selfUrl;
    private URI cityUrl;

    public static UniversityDto fromUniversity(final UriInfo uriInfo, final University university) {
        final UniversityDto dto = new UniversityDto();
        dto.id = university.getId();
        dto.name = university.getName();
        dto.abbreviation = university.getAbbreviation();
        dto.selfUrl = UriUtils.getUniversityUri(uriInfo, university.getId());
        dto.cityUrl = UriUtils.getCityUri(uriInfo, university.getCity().getId());
        return dto;
    }

    public static List<UniversityDto> fromUniversityCollection(final UriInfo uriInfo, final Collection<University> universities) {
        return universities.stream().map(university -> fromUniversity(uriInfo, university)).toList();
    }


    public long getId() { return id; }
    public String getName() { return name; }
    public String getAbbreviation() { return abbreviation; }
    public URI getSelfUrl() { return selfUrl; }
    public URI getCityUrl() { return cityUrl; }
}
