package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

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
}
