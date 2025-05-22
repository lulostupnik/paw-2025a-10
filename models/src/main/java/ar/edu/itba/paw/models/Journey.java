package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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
    @Column(name = "deleted_message", length = 2047)
    @Setter
    private String deletionMessage;


    /* For hibernate */ Journey() {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{journeyId: ");
        sb.append(id);
        sb.append(", user: ");
        sb.append(user);
        sb.append(", destinationUniversity: ");
        sb.append(destinationUniversity);
        sb.append(", startDate: \"");
        sb.append(startDate);
        sb.append("\", endDate: \"");
        sb.append(endDate);
        sb.append("\", description: \"");
        sb.append(description);
        sb.append("\"}");
        return sb.toString();
    }
}

