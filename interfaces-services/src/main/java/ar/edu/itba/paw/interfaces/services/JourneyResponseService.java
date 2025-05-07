package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.time.LocalDateTime;
import java.util.List;

public interface JourneyResponseService {
    JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime);
    List<JourneyResponse> listAllFromJourney(long journeyId);
    void delete(long id, String message);
    long getJourneyIdByResponseId(long journeyId);
    void deleteByJourneyId(long journeyId);

    public Page<JourneyResponse> listAllFromJourney(long eventId, PageParams pageParams);

    int getCount(long id);
}
