package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.time.LocalDateTime;
import java.util.Optional;

public interface JourneyResponseDao {
    JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime);
    Page<JourneyResponse> findAllByJourneyId(long journeyId, PageParams pageParams);
    Optional<JourneyResponse> findById(long id);

    void delete(long id);
    long findJourneyIdByResponseId(long id);
    void updateDeletionMessage(long id, String message);
    void deleteByJourneyId(long journeyId);
    int countByJourneyId(long journeyId);
}

