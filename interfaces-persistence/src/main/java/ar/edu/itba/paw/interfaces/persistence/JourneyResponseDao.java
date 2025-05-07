package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface JourneyResponseDao {
    JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime);
    List<JourneyResponse> listAllFromJourney(long journeyId);

    Page<JourneyResponse> listAllFromJourney(long journeyId, int page, int pageSize);

   // List<EmailRecipient> listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds);
    void delete(long id);
    long getJourneyIdByResponseId(long journeyId);
    void deletionMessage(long id, String message);
    void deleteByJourneyId(long journeyId);

    int getCount(long id);
}

