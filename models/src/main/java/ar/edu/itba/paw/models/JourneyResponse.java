package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@Table(name = "journey_responses")
public class JourneyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journey_responses_id_seq")
    @SequenceGenerator(sequenceName = "journey_responses_id_seq", name = "journey_responses_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private  User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journey_id", nullable = false)
    private  Journey journey;

    @Column(length = 1023, nullable = false)
    private String message;

    @Column(name = "date_time", nullable = false)
    private  LocalDateTime dateTime;

    @Column(name="deleted", nullable = false)
    @ToString.Exclude
    @Setter
    private  boolean deleted;

    @Column(name = "deleted_message", length = 2047)
    @ToString.Exclude
    @Setter
    private  String deletionMessage;

    /* For hibernate */ JourneyResponse() {
    }

    public JourneyResponse(final User user, final Journey journey, final String message) {
        this.user = user;
        this.journey = journey;
        this.message = message;
        this.dateTime = LocalDateTime.now();
        this.deleted = false;
    }
    public JourneyResponse(final Long id, final User user, final Journey journey, final String message, final LocalDateTime time) {
        this.id = id;
        this.user = user;
        this.journey = journey;
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
        sb.append(", journeyId: ");
        sb.append(journey);
        sb.append(", message: \"");
        sb.append(message);
        sb.append("\"}");
        return sb.toString();
    }
}

