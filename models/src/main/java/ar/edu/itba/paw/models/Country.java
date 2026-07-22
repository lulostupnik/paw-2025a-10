package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Objects;

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
    public Country(final Long id, final String name, final String code) {
        this.name = name;
        this.code = code;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country country)) return false;
        return id != null && id.equals(country.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code);
    }
}
