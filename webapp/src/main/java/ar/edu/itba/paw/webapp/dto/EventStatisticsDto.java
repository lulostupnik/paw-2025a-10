package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.CountryAttendeeCount;
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
    private Integer topCountryCount;
    private int totalParticipants;
    private Integer maxParticipants;
    private Links links;

    public static EventStatisticsDto fromEventWithStatistics(final UriInfo uriInfo, final EventWithStatistics statistics) {
        final EventStatisticsDto dto = new EventStatisticsDto();
        final Event event = statistics.getEvent();

        dto.eventsCreatedByOrganizer = statistics.getCreatedEventsCount();
        dto.eventsOrganizerAttends = statistics.getAttendedEventsCount();

        dto.totalParticipants = event.getAttendeesCount();
        // sin límite de asistentes el campo se omite, en vez de afirmar un límite de 0
        dto.maxParticipants = event.getAttendeesLimit();

        final Links links = new Links();
        links.selfUrl = UriUtils.getEventStatisticsUri(uriInfo, event.getId());
        links.eventUrl = UriUtils.getEventUri(uriInfo, event.getId());

        final CountryAttendeeCount topCountry = statistics.getTopAttendeeCountry();
        if (topCountry != null) {
            dto.topCountry = topCountry.getCountryName();
            dto.topCountryCount = topCountry.getCount();
            links.topCountryUrl = UriUtils.getCountryUri(uriInfo, topCountry.getCountryId());
        }
        dto.links = links;

        return dto;
    }

    public int getEventsCreatedByOrganizer() { return eventsCreatedByOrganizer; }
    public int getEventsOrganizerAttends() { return eventsOrganizerAttends; }
    public String getTopCountry() { return topCountry; }
    public Integer getTopCountryCount() { return topCountryCount; }
    public int getTotalParticipants() { return totalParticipants; }
    public Integer getMaxParticipants() { return maxParticipants; }
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
