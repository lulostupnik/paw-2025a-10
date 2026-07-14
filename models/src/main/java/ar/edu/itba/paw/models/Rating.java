package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}, name = "unique_user_event_rating"))
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ratings_id_seq")
    @SequenceGenerator(sequenceName = "ratings_id_seq", name = "ratings_id_seq", allocationSize = 1)
    @Column(name = "id")
    @Getter
    private Long id;
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @Setter
    @Getter
    @Column(name = "rating", nullable = false)
    private double rating;

    /* For hibernate */ Rating() {
    }

    public Rating(User user, Event event, double rating) {
        this.user = user;
        this.event = event;
        this.rating = rating;
    }
    
    public Rating(long id, User user, Event event, double rating) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating rating1)) return false;
        return id != null && id.equals(rating1.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                user != null ? user.getId() : null,
                event != null ? event.getId() : null,
                rating
        );
    }


}
