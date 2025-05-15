package ar.edu.itba.paw.models;

import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
@Entity
@Table(name = "event_responses")
public class EventResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "event_responses_id_seq")
    @SequenceGenerator(sequenceName = "event_responses_id_seq", name =
            "event_responses_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private  User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private  Event event;
    @Column(length = 2047)
    private  String message;
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
    @Column(name="deleted", nullable = false)
    private  boolean deleted;

    /* For hibernate */ EventResponse() {
    }
    public EventResponse(final User user, final Event event, final String message) {
        this.user = user;
        this.event = event;
        this.message = message;
        this.dateTime = LocalDateTime.now();
        this.deleted = false;
    }
    public EventResponse(final Long id, final User user, final Event event, final String message, final LocalDateTime time) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.message = message;
        this.dateTime = time;
        this.deleted = false;
    }

    public String getFormattedDate() {
        return dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{userId: ");
        sb.append(user);
        sb.append(", eventId: ");
        sb.append(event);
        sb.append(", message: \"");
        sb.append(message);
        sb.append("\"}");
        return sb.toString();
    }
}
