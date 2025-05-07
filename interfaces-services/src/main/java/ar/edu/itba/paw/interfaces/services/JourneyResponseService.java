package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JourneyResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JourneyResponseService {
    JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime);
    List<JourneyResponse> listAllFromJourney(long journeyId);
    void delete(long id, String message);
    long getJourneyIdByResponseId(long journeyId);
    void deleteByJourneyId(long journeyId);

    Optional<JourneyResponse> findById(long id);
}
