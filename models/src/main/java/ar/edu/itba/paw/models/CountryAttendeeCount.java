package ar.edu.itba.paw.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class CountryAttendeeCount {

    private final long countryId;
    private final String countryName;
    private final int count;

}