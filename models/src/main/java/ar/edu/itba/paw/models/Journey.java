package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Entity
@Table(name = "journeys")
public class Journey{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journeys_id_seq")
    @SequenceGenerator(sequenceName = "journeys_id_seq", name = "journeys_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "start_date", nullable = false)
    @Setter
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @Setter
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "destination_university_id", nullable = false)
    @Setter
    private University destinationUniversity;

    @Column(length = 2047)
    @Setter
    private String description;

    @Column(name="deleted", nullable = false)
    @Setter
    private boolean deleted;

    @Column(name = "deleted_message", length = 1000)
    @Setter
    private String deletionMessage;


     Journey() {
    }

    public Journey(final User user, final LocalDate startDate, final LocalDate endDate,
                   final University destinationUniversity, final String description) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destinationUniversity = destinationUniversity;
        this.description = description;
        this.deleted = false;
    }
    public Journey(final Long id, final User user, final LocalDate startDate, final LocalDate endDate,
                   final University destinationUniversity, final String description) {
        this.id = id;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destinationUniversity = destinationUniversity;
        this.description = description;
        this.deleted = false;
    }
    public Journey(final Long id, final User user, final LocalDate startDate, final LocalDate endDate,
                   final University destinationUniversity, final String description, final boolean deleted) {
        this.id = id;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destinationUniversity = destinationUniversity;
        this.description = description;
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "{journeyId: " +
                id +
                ", user: " +
                user +
                ", destinationUniversity: " +
                destinationUniversity +
                ", startDate: \"" +
                startDate +
                "\", endDate: \"" +
                endDate +
                "\", description: \"" +
                description +
                "\"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journey journey)) return false;
        return id != null && id.equals(journey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                startDate,
                endDate,
                destinationUniversity != null ? destinationUniversity.getId() : null,
                destinationUniversity != null ? destinationUniversity.getName() : null,
                description,
                deleted,
                deletionMessage
        );
    }
}

