package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.EventAttendance;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EventAttendanceDto {
    // TODO: El DTO puede estar "vacío"? O deberíamos poner acá userId y eventId o algo por el estilo?

    private URI userUrl;
    private URI eventUrl;
    private URI selfUrl;

    public static EventAttendanceDto fromEventAttendance(final UriInfo uriInfo, final EventAttendance attendance) {
        final EventAttendanceDto dto = new EventAttendanceDto();
        dto.userUrl = UriUtils.getUserUri(uriInfo, attendance.getUser().getId());
        dto.eventUrl = UriUtils.getEventUri(uriInfo, attendance.getEvent().getId());
        dto.selfUrl = UriUtils.getEventAttendanceUri(uriInfo, attendance.getEvent().getId(), attendance.getUser().getId());
        return dto;
    }

    public static List<EventAttendanceDto> fromEventAttendanceCollection(final UriInfo uriInfo, final Collection<EventAttendance> attendances) {
        return attendances.stream().map(a -> fromEventAttendance(uriInfo, a)).toList();
    }

    public URI getUserUrl() {
        return userUrl;
    }

    public URI getEventUrl() {
        return eventUrl;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }
}
