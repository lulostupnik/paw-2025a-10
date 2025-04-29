package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.valueObjects.EmailRecipient;

import java.time.LocalDateTime;
import java.util.List;

public interface EventResponseService {
    EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime);
    void delete(long id,String message);
    List<EmailRecipient> listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds);
    long getEventIdByResponseId(long eventId);
    List<EventResponse> listAllFromEvent(long eventId);

}
