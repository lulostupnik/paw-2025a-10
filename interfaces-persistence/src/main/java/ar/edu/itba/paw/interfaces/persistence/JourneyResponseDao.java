package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.JourneyResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface JourneyResponseDao {
    JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime);
    List<JourneyResponse> listAllFromJourney(long journeyId);

    String[] listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds);
    void delete(long id);
}

