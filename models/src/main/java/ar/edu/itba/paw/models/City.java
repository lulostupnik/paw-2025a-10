package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@Table(name = "cities")
public class City {
    @Setter
    @Column(nullable = false, length = 100, unique=true)
    private  String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    @Setter
    private  Country country;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "cities_id_seq")
    @SequenceGenerator(sequenceName = "cities_id_seq", name =
            "cities_id_seq", allocationSize = 1)
    private  Long id;
    @Setter
    @Column(name="deleted", nullable = false)
    private  boolean deleted;

    /* For hibernate */ City() {
    }
    public City(final String name, final Country country) {
        this.name = name;
        this.country = country;
        this.deleted = false;
    }
    public City(final String name, final Country country, final Long id) {
        this.name = name;
        this.country = country;
        this.id = id;
        this.deleted = false;
    }

    @Override
    public String toString() {
        return name + ", " + country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City city)) return false;
        return id != null && id.equals(city.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                country != null ? country.getId() : null,
                deleted
        );
    }

    }


