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

    private URI selfUrl;
    private URI userUrl;
    private URI destinationUniversityUrl;
    private URI tipsUrl;
    private URI responsesUrl;

    public static JourneyDto fromJourney(final UriInfo uriInfo, final Journey journey) {
        final JourneyDto dto = new JourneyDto();
        dto.id = journey.getId();
        dto.description = journey.getDescription();
        dto.startDate = journey.getStartDate();
        dto.endDate = journey.getEndDate();

        dto.selfUrl = UriUtils.getJourneyUri(uriInfo, journey.getId());
        dto.userUrl = UriUtils.getUserUri(uriInfo, journey.getUser().getId());
        dto.destinationUniversityUrl = UriUtils.getUniversityUri(uriInfo, journey.getDestinationUniversity().getId()); // TODO: revisar
        dto.tipsUrl = UriUtils.getJourneyTipsUri(uriInfo, journey.getId());
        dto.responsesUrl = UriUtils.getJourneyResponsesUri(uriInfo, journey.getId());

        return dto;
    }

    public static List<JourneyDto> fromJourneyCollection(final UriInfo uriInfo, final Collection<Journey> journeys) {
        return journeys.stream().map(journey -> fromJourney(uriInfo, journey)).toList();
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public URI getSelfUrl() { return selfUrl; }
    public URI getUserUrl() { return userUrl; }
    public URI getDestinationUniversityUrl() { return destinationUniversityUrl; }
    public URI getTipsUrl() { return tipsUrl; }
    public URI getResponsesUrl() { return responsesUrl; }
}
