package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "event_responses")
public class EventResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_responses_id_seq")
    @SequenceGenerator(sequenceName = "event_responses_id_seq", name = "event_responses_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private  User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private  Event event;

    @Column(length = 2047, nullable = false)
    private  String message;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name="deleted", nullable = false)
    @Setter
    private  boolean deleted;

    @Column(name = "deleted_message", length = 1000)
    @Setter
    private  String deletionMessage;

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

    @Override
    public String toString() {
        return "{userId: " +
                user +
                ", eventId: " +
                event +
                ", message: \"" +
                message +
                "\"}";
    }
}
