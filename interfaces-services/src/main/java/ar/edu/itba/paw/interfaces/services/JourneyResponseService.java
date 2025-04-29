package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.valueObjects.EmailRecipient;

import java.time.LocalDateTime;
import java.util.List;

public interface JourneyResponseService {
    JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime);
    List<JourneyResponse> listAllFromJourney(long journeyId);
    List<EmailRecipient> listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds);
    void delete(long id, String message);
    long getJourneyIdByResponseId(long journeyId);
}
