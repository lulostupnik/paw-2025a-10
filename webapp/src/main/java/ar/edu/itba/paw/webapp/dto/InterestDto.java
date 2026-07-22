package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Interest;
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
public class InterestDto {

    private long id;
    private String name;

    private Links links;

    public static InterestDto fromInterest(final UriInfo uriInfo, final Interest interest) {
        final InterestDto dto = new InterestDto();
        dto.id = interest.getId();
        dto.name = interest.getName();
        final Links links = new Links();
        links.selfUrl = UriUtils.getInterestUri(uriInfo, interest.getId());
        dto.links = links;
        return dto;
    }

    public static List<InterestDto> fromInterestCollection(final UriInfo uriInfo, final Collection<Interest> interests) {
        return interests.stream().map(interest -> fromInterest(uriInfo, interest)).toList();
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
