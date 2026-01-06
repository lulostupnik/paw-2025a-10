package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JourneyResponseDto {

    private long id;
    private String message;
    private LocalDateTime dateTime;

    private URI selfUrl;
    private URI journeyUrl;
    private URI authorUrl;

    public static JourneyResponseDto fromJourneyResponse(final UriInfo uriInfo, final JourneyResponse response) {
        final JourneyResponseDto dto = new JourneyResponseDto();
        dto.id = response.getId();
        dto.message = response.getMessage();
        dto.dateTime = response.getDateTime();

        dto.journeyUrl = UriUtils.getJourneyUri(uriInfo, response.getJourney().getId());
        dto.selfUrl = UriUtils.getJourneyResponseUri(uriInfo, response.getJourney().getId(), response.getId());
        dto.authorUrl = UriUtils.getUserUri(uriInfo, response.getUser().getId());

        return dto;
    }

    public static List<JourneyResponseDto> fromJourneyResponseCollection(final UriInfo uriInfo, final Collection<JourneyResponse> responses) {
        return responses.stream().map(response -> fromJourneyResponse(uriInfo, response)).toList();
    }

    public long getId() { return id; }
    public String getMessage() { return message; }
    public LocalDateTime getDateTime() { return dateTime; }
    public URI getSelfUrl() { return selfUrl; }
    public URI getJourneyUrl() { return journeyUrl; }
    public URI getAuthorUrl() { return authorUrl; }
}
