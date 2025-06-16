package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tips")
public class Tip {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tips_id_seq")
    @SequenceGenerator(sequenceName = "tips_id_seq", name = "tips_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name="title", nullable = false)
    private String title;
    @Column(name = "content", length = 2047, nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;
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

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Journey getJourney() {
        return journey;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setJourney(Journey journey) {
        this.journey = journey;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
