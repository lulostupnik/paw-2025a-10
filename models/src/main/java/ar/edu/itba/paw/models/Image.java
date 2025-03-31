package ar.edu.itba.paw.models;

public class Image {
    private final Long id;
    private final byte[] data;

    public Image(Long id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

}
