package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name="category")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "category_id_seq")
    @SequenceGenerator(sequenceName = "category_id_seq", name =
            "category_id_seq", allocationSize = 1)
    private  Long id;
    @Column(nullable = false, length = 100)
    private  String name;
    @Column(name="deleted", nullable = false)
    private  boolean deleted;

    /* For hibernate */ Interest() {
    }
    public Interest(final String name) {
        this.name = name;
        this.deleted = false;
    }
    public Interest(final Long id, final String name) {
        this.name = name;
        this.id = id;
        this.deleted = false;
    }
    @Override
    public String toString() {
        return name;
    }

}
