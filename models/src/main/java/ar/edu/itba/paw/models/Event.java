package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class Event {
    private final long id;
    private final User user;
    private final Date date;
    private final String description;
    private final long flyerImageId;
    private final City eventCity;
}
