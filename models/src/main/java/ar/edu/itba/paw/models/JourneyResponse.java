package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JourneyResponse {
    private final long userId;
    private final long journeyId;
    private final String message;
}
