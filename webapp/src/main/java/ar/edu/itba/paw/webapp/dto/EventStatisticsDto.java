package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.EventWithStatistics;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EventStatisticsDto {

    private int eventsCreatedByOrganizer;
    private int eventsOrganizerAttends;
    private String topCountry;
    private int topCountryCount;
    private int totalParticipants;
    private int maxParticipants;
    private Links links;

    public static EventStatisticsDto fromEventWithStatistics(final UriInfo uriInfo, final EventWithStatistics statistics) {
        final EventStatisticsDto dto = new EventStatisticsDto();
        final Event event = statistics.getEvent();

        dto.eventsCreatedByOrganizer = statistics.getCreatedEventsCount();
        dto.eventsOrganizerAttends = statistics.getAttendedEventsCount();

        String country = statistics.getTopAttendeeCountry();
        int countryCount = statistics.getTopAttendeeCountryCount();

        if (country == null || country.isBlank()) {
            //@TODO creo que tienen que ser consistentes los campos entonces pongo esto
            country = "";
        }

        dto.topCountry = country;
        dto.topCountryCount = countryCount;
        dto.totalParticipants = event.getAttendeesCount();
        final Integer limit = event.getAttendeesLimit();
        dto.maxParticipants = (limit == null || limit <= 0) ? 0 : limit;
        final Links links = new Links();
        links.selfUrl = UriUtils.getEventStatisticsUri(uriInfo, event.getId());
        links.eventUrl = UriUtils.getEventUri(uriInfo, event.getId());
        if (statistics.getTopAttendeeCountryId() != null) {
            links.topCountryUrl = UriUtils.getCountryUri(uriInfo, statistics.getTopAttendeeCountryId());
        }
        dto.links = links;

        return dto;
    }

    public int getEventsCreatedByOrganizer() { return eventsCreatedByOrganizer; }
    public int getEventsOrganizerAttends() { return eventsOrganizerAttends; }
    public String getTopCountry() { return topCountry; }
    public int getTopCountryCount() { return topCountryCount; }
    public int getTotalParticipants() { return totalParticipants; }
    public int getMaxParticipants() { return maxParticipants; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI eventUrl;
        private URI topCountryUrl; 

        public URI getSelfUrl() { return selfUrl; }
        public URI getEventUrl() { return eventUrl; }
        public URI getTopCountryUrl() { return topCountryUrl; }
    }
}
