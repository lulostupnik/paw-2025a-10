package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "universities")
public class University{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
            "universities_id_seq")
    @SequenceGenerator(sequenceName = "universities_id_seq", name =
            "universities_id_seq", allocationSize = 1)
    @Column(name = "id")
    private  Long id;
    @Column()
    private  String name;
    @Column(name = "abbreviation")
    private  String abbreviation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private  City city;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" (");
        sb.append(abbreviation);
        sb.append(")");
        return sb.toString();
    }

}