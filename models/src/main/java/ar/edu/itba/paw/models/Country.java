package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "countries_id_seq")
    @SequenceGenerator(sequenceName = "countries_id_seq", name =
            "countries_id_seq", allocationSize = 1)
    private  Long id;
    @Column(nullable = false, length = 100)
    @Setter
    private  String name;
    @Setter
    @Column(name = "code", length = 3)
    private  String code;

    Country(){

    }
    public Country(final String name, final String code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return name;
    }
}
