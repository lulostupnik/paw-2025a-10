package ar.edu.itba.paw.models;

import lombok.Getter;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="event_attendances")
@Getter
public class EventAttendance {
    @EmbeddedId
    private EventAttendanceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id",nullable = false)
    private Event event;

    EventAttendance() {}
    public EventAttendance(final User user, final Event event) {
        this.user = user;
        this.event = event;
        this.id = new EventAttendanceId(user.getId(), event.getId());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventAttendance that)) return false;
        return id.equals(that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
