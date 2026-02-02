package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JourneyDto {

    private long id;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    private Links links;

    public static JourneyDto fromJourney(final UriInfo uriInfo, final Journey journey) {
        final JourneyDto dto = new JourneyDto();
        dto.id = journey.getId();
        dto.description = journey.getDescription();
        dto.startDate = journey.getStartDate();
        dto.endDate = journey.getEndDate();

        final Links links = new Links();
        links.selfUrl = UriUtils.getJourneyUri(uriInfo, journey.getId());
        links.userUrl = UriUtils.getUserUri(uriInfo, journey.getUser().getId());
        links.destinationUniversityUrl = UriUtils.getUniversityUri(uriInfo, journey.getDestinationUniversity().getId()); // TODO: revisar
        links.tipsUrl = UriUtils.getJourneyTipsUri(uriInfo, journey.getId());
        links.responsesUrl = UriUtils.getJourneyResponsesUri(uriInfo, journey.getId());
        dto.links = links;

        return dto;
    }

    public static List<JourneyDto> fromJourneyCollection(final UriInfo uriInfo, final Collection<Journey> journeys) {
        return journeys.stream().map(journey -> fromJourney(uriInfo, journey)).toList();
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI userUrl;
        private URI destinationUniversityUrl;
        private URI tipsUrl;
        private URI responsesUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getUserUrl() { return userUrl; }
        public URI getDestinationUniversityUrl() { return destinationUniversityUrl; }
        public URI getTipsUrl() { return tipsUrl; }
        public URI getResponsesUrl() { return responsesUrl; }
    }
}
