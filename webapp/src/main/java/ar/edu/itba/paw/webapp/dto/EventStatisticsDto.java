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
    private TotalParticipants totalParticipants;
    private URI selfUrl;
    private URI eventUrl;

    public static EventStatisticsDto fromEventWithStatistics(final UriInfo uriInfo, final EventWithStatistics statistics) {
        final EventStatisticsDto dto = new EventStatisticsDto();
        final Event event = statistics.getEvent();

        dto.eventsCreatedByOrganizer = statistics.getCreatedEventsCount();
        dto.eventsOrganizerAttends = statistics.getAttendedEventsCount();

        String country = statistics.getTopAttendeeCountry();
        int countryCount = statistics.getTopAttendeeCountryCount();
        if (country == null || country.isBlank()) {
            if (event.getCity() != null && event.getCity().getCountry() != null) {
                country = event.getCity().getCountry().getName();
            } else {
                country = "";
            }
            countryCount = event.getAttendeesCount();
        }

        dto.topCountry = country;
        dto.topCountryCount = countryCount;
        dto.totalParticipants = TotalParticipants.fromEvent(event);
        dto.selfUrl = UriUtils.getEventStatisticsUri(uriInfo, event.getId());
        dto.eventUrl = UriUtils.getEventUri(uriInfo, event.getId());

        return dto;
    }

    public int getEventsCreatedByOrganizer() { return eventsCreatedByOrganizer; }
    public int getEventsOrganizerAttends() { return eventsOrganizerAttends; }
    public String getTopCountry() { return topCountry; }
    public int getTopCountryCount() { return topCountryCount; }
    public TotalParticipants getTotalParticipants() { return totalParticipants; }
    public URI getSelfUrl() { return selfUrl; }
    public URI getEventUrl() { return eventUrl; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TotalParticipants {
        private int count;
        private int maxParticipants;

        private static TotalParticipants fromEvent(final Event event) {
            final TotalParticipants dto = new TotalParticipants();
            dto.count = event.getAttendeesCount();
            final Integer limit = event.getAttendeesLimit();
            dto.maxParticipants = (limit == null || limit <= 0) ? 0 : limit;
            return dto;
        }

        public int getCount() { return count; }
        public int getMaxParticipants() { return maxParticipants; }
    }
}
