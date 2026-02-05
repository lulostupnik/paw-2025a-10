package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Career;
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
public class CareerDto {

    private long id;
    private String name;

    private Links links;

    public static CareerDto fromCareer(final UriInfo uriInfo, final Career career) {
        final CareerDto dto = new CareerDto();
        dto.id = career.getId();
        dto.name = career.getName();
        final Links links = new Links();
        links.selfUrl = UriUtils.getCareerUri(uriInfo, career.getId());
        dto.links = links;
        return dto;
    }

    public static List<CareerDto> fromCareerCollection(final UriInfo uriInfo, final Collection<Career> careers) {
        return careers.stream().map(career -> fromCareer(uriInfo, career)).toList();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;

        public URI getSelfUrl() { return selfUrl; }
    }
}
