package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.EventResponse;
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
public class EventResponseDto {

    private long id;
    private String message;
    private LocalDateTime dateTime;

    private Links links;

    public static EventResponseDto fromEventResponse(final UriInfo uriInfo, final EventResponse response) {
        final EventResponseDto dto = new EventResponseDto();
        dto.id = response.getId();
        dto.message = response.getMessage();
        dto.dateTime = response.getDateTime();

        final Links links = new Links();
        links.eventUrl = UriUtils.getEventUri(uriInfo, response.getEvent().getId());
        links.selfUrl = UriUtils.getEventResponseUri(uriInfo, response.getEvent().getId(), response.getId());
        links.authorUrl = UriUtils.getUserUri(uriInfo, response.getUser().getId());
        dto.links = links;

        return dto;
    }

    public static List<EventResponseDto> fromEventResponseCollection(final UriInfo uriInfo, final Collection<EventResponse> responses) {
        return responses.stream().map(response -> fromEventResponse(uriInfo, response)).toList();
    }

    public long getId() { return id; }
    public String getMessage() { return message; }
    public LocalDateTime getDateTime() { return dateTime; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI eventUrl;
        private URI authorUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getEventUrl() { return eventUrl; }
        public URI getAuthorUrl() { return authorUrl; }
    }
}
