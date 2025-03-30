package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JourneyResponse;

public interface JourneyResponseDao {
    JourneyResponse create(long userId, long journeyId, String message);
}
