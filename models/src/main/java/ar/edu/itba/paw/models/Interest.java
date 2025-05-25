package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name="category")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_seq")
    @SequenceGenerator(sequenceName = "category_id_seq", name = "category_id_seq", allocationSize = 1)
    private  Long id;

    @Column(nullable = false, length = 100, unique = true)
    @Setter
    private String name;


    /* For hibernate */ Interest() {
    }
    public Interest(final String name) {
        this.name = name;
    }
    public Interest(final Long id, final String name) {
        this.name = name;
        this.id = id;
    }
    @Override
    public String toString() {
        return name;
    }

}
