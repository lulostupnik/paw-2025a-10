package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collection;
import java.util.List;

// TODO: revisar

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RatingDto {

    private long id;
    private double rating;

    private Links links;

    public static RatingDto fromRating(final UriInfo uriInfo, final Rating rating) {
        final RatingDto dto = new RatingDto();
        dto.id = rating.getId();
        dto.rating = rating.getRating();

        final Links links = new Links();
        links.eventUrl = UriUtils.getEventUri(uriInfo, rating.getEvent().getId());
        links.selfUrl = UriUtils.getEventRatingUri(uriInfo, rating.getEvent().getId(), rating.getId());
        links.userUrl = UriUtils.getUserUri(uriInfo, rating.getUser().getId());
        dto.links = links;

        return dto;
    }

    public static List<RatingDto> fromRatingCollection(final UriInfo uriInfo, final Collection<Rating> ratings) {
        return ratings.stream().map(r -> fromRating(uriInfo, r)).toList();
    }

    public long getId() { return id; }
    public double getRating() { return rating; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI eventUrl;
        private URI userUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getEventUrl() { return eventUrl; }
        public URI getUserUrl() { return userUrl; }
    }
}
