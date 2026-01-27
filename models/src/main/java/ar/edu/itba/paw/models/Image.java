package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.ToString;
import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_id_seq")
    @SequenceGenerator(sequenceName = "images_id_seq", name = "images_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "content", nullable = false)
    private byte[] data;

    public Image() {
    }
    public Image(final byte[] data) {
        this.data = data;
    }
    public Image(final Long id, final byte[] data) {
        this.id = id;
        this.data = data;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image image)) return false;
        return id != null && id.equals(image.id);
    }
    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + java.util.Arrays.hashCode(data);
        return result;
    }
}
