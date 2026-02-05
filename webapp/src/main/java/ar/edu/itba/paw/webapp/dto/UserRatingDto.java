package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRatingDto {

    private Double attendedEventsRating;
    private Double hostedEventsRating;
    // private int totalRatings;
    private Links links;

    public static UserRatingDto fromRatings(final UriInfo uriInfo, final long userId, final Double attendedEventsRating, final Double hostedEventsRating) {
        final UserRatingDto dto = new UserRatingDto();
        dto.hostedEventsRating = hostedEventsRating;
        dto.attendedEventsRating = attendedEventsRating;
        final Links links = new Links();
        links.selfUrl = UriUtils.getUserRatingUri(uriInfo, userId);
        links.userUrl = UriUtils.getUserUri(uriInfo, userId);
        dto.links = links;
        return dto;
    }

    public Double getAttendedEventsRating() {
        return attendedEventsRating;
    }
    public Double getHostedEventsRating() {
        return hostedEventsRating;
    }

    public Links getLinks() {
        return links;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI userUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getUserUrl() { return userUrl; }
    }
}
