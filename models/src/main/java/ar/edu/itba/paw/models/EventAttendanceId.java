package ar.edu.itba.paw.models;

import lombok.Getter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class EventAttendanceId implements Serializable {
    private Long userId;
    private Long eventId;
    public EventAttendanceId(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public EventAttendanceId() {}
    // equals() y hashCode() son necesarios para claves compuestas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventAttendanceId)) return false;
        EventAttendanceId that = (EventAttendanceId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }
}
