package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.UserRating;
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
    private Links links;

    public static UserRatingDto fromUserRating(final UriInfo uriInfo, final UserRating rating) {
        final UserRatingDto dto = new UserRatingDto();
        dto.hostedEventsRating = rating.getCreatedEventsRating();
        dto.attendedEventsRating = rating.getAttendedEventsRating();
        final Links links = new Links();
        links.selfUrl = UriUtils.getUserRatingUri(uriInfo, rating.getUserId());
        links.userUrl = UriUtils.getUserUri(uriInfo, rating.getUserId());
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
