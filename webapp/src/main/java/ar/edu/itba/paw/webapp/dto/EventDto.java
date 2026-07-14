package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EventDto {

    private long id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String address;
    private Integer attendeesLimit;
    private int attendeesCount;
    private Double rating;
    private String cityName;

    private Links links;

    public static EventDto fromEvent(final UriInfo uriInfo, final Event event) {
        final EventDto dto = new EventDto();
        dto.id = event.getId();
        dto.title = event.getTitle();
        dto.description = event.getDescription();
        dto.date = event.getDate();
        dto.time = event.getTime();
        dto.address = event.getAddress();
        dto.attendeesLimit = event.getAttendeesLimit();
        dto.attendeesCount = event.getAttendeesCount();
        dto.rating = event.getRating();
        dto.cityName = event.getCity().getName();

        final Links links = new Links();
        links.selfUrl = UriUtils.getEventUri(uriInfo, event.getId());
        links.creatorUrl = UriUtils.getUserUri(uriInfo, event.getUser().getId());
        links.cityUrl = UriUtils.getCityUri(uriInfo, event.getCity().getId());
        links.flyerUrl = event.getFlyerImageId() != null ? UriUtils.getEventFlyerUri(uriInfo, event.getId()) : null;
        links.responsesUrl = UriUtils.getEventResponsesUri(uriInfo, event.getId());
        links.attendancesUrl = UriUtils.getEventAttendancesUri(uriInfo, event.getId());
        links.ratingsUrl = UriUtils.getEventRatingsUri(uriInfo, event.getId());
        links.statisticsUrl = UriUtils.getEventStatisticsUri(uriInfo, event.getId());
        dto.links = links;

        return dto;
    }

    public static List<EventDto> fromEventCollection(final UriInfo uriInfo, final Collection<Event> events) {
        return events.stream().map(event -> fromEvent(uriInfo, event)).toList();
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getAddress() { return address; }
    public Integer getAttendeesLimit() { return attendeesLimit; }
    public int getAttendeesCount() { return attendeesCount; }
    public Double getRating() { return rating; }
    public String getCityName() { return cityName; }
    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI creatorUrl;
        private URI cityUrl;
        private URI flyerUrl;
        private URI responsesUrl;
        private URI attendancesUrl; // TODO: revisar
        private URI ratingsUrl;
        private URI statisticsUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getCreatorUrl() { return creatorUrl; }
        public URI getCityUrl() { return cityUrl; }
        public URI getFlyerUrl() { return flyerUrl; }
        public URI getResponsesUrl() { return responsesUrl; }
        public URI getAttendancesUrl() { return attendancesUrl; }
        public URI getRatingsUrl() { return ratingsUrl; }
        public URI getStatisticsUrl() { return statisticsUrl; }
    }
}
