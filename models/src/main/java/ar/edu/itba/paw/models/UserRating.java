package ar.edu.itba.paw.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class UserRating {
    private final long userId;
    private final Double createdEventsRating;
    private final Double attendedEventsRating;
}
