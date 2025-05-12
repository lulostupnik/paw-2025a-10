package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class Image {
    private final long id;
    private final byte[] data;
}
