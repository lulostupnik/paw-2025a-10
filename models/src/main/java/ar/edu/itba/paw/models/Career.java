package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name="careers")
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "careers_id_seq")
    @SequenceGenerator(sequenceName = "careers_id_seq", name =
            "careers_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;
    @Column(nullable = false, length = 255)
    private  String name;

    public Career() {
    }
    public Career(final String name) {
        this.name = name;
    }
    public Career(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
