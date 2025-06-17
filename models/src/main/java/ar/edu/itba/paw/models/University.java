package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@Table(name = "universities")
public class University{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "universities_id_seq")
    @SequenceGenerator(sequenceName = "universities_id_seq", name = "universities_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 255)
    private String name;


    @Setter
    @Column(name = "abbreviation", length = 255)
    private String abbreviation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    @Setter
    private  City city;

    @Setter
    @Column(name="deleted", nullable = false)
    private  boolean deleted;

    University(){

    }
    public University(final String name, final String abbreviation, final City city) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.city = city;
        this.deleted = false;
    }

    public University( final Long id, final String name, final String abbreviation, final City city) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.city = city;
        this.id = id;
        this.deleted = false;
    }

    @Override
    public String toString() {
        return name +
                " (" +
                abbreviation +
                ")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof University that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}