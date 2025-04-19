package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JourneyService {

    Journey createJourney(String email, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description);

    void replyToJourney(String email, long journeyId, String message);

    List<Journey> getAllJourneys();

    Optional<Journey> getJourneyById(long id);

    List<Journey> getFilteredJourneys(String destination, LocalDate startDate, LocalDate endDate, String interest);

    Boolean userHasJourney(String email); // ja

    List<Journey> getRecommendedJourneys(String email);

    List<JourneyResponse> getJourneyResponses(long journeyId);

    CursorPage<JourneyResponse, LocalDateTime> listFromJourneyAfter(long journeyId, LocalDateTime cursor, int limit);

    CursorPage<Journey, Long> findByFilters(String destination, LocalDate startDate, LocalDate endDate, String interest, Long cursor, int pageSize);

    CursorPage<Journey, Long> listAll(Long cursor, int pageSize);

}
