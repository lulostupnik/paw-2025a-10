package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tips")
public class Tip {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tips_id_seq")
    @SequenceGenerator(sequenceName = "tips_id_seq", name = "tips_id_seq", allocationSize = 1)
    @Column(name = "id")
    @Getter
    private Long id;
    @Getter
    @Setter
    @Column(name="title", nullable = false)
    private String title;
    @Getter
    @Setter
    @Column(name = "content", length = 2047, nullable = false)
    private String content;
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;
    @Getter
    @Setter
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    public Tip(long id, String title, String content, Journey journey, LocalDateTime dateTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.journey = journey;
        this.dateTime = dateTime;
    }
    Tip(){}

    public Tip(Journey journey, String title, String content) {
        this.journey = journey;
        this.title = title;
        this.content = content;
        this.dateTime = LocalDateTime.now();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tip tip)) return false;
        return id != null && id.equals(tip.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}
