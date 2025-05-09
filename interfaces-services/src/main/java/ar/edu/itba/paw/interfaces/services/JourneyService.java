package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JourneyService {

    Journey createJourney(User user, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description);

    void replyToJourney(String email, long journeyId, String message);

    List<Journey> getAllJourneys();

    Page<Journey> getAllJourneys(String search, PageParams pageParams);

    Optional<Journey> getJourneyById(long id);

    Optional<Journey> getJourneyByEmail(String email);

    Page<Journey> getAllJourneys(String search, User user, String sortBy, String direction, String destination, LocalDate startDate, LocalDate endDate, String interest, boolean isPast, boolean isUpcoming, boolean isMyDestination, PageParams pageParams);

    Boolean userHasJourney(String email); // ja

    boolean userHasJourney(User user);

    List<Journey> getRecommendedJourneys(String email, int limit);

    List<Journey> getJourneysByUser(String email);


    List<Journey> getOthersJourneys(long userId);

    List<Journey> getOthersJourneys(String email);

    void updateJourneyDates(long journeyId, LocalDate startDate, LocalDate endDate);

    void updateJourneyDescription(long journeyId, String description);

    void updateJourneyDestination(long journeyId, String universityName);

    void updateJourneyDestination(long journeyId, long universityId);

    void delete(long id, String message);

    boolean isJourneyOwnedByUser(String email, long journeyID);

    void editJourney(long journeyId, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description);

    List<JourneyResponse> listAllResponsesFromJourney(long journeyId);
    void deleteJourneyResponse(long id, String message);
    long getJourneyIdByResponseId(long journeyId);

    Optional<JourneyResponse> findJourneyResponseById(long id);

    Page<JourneyResponse> listAllResponsesFromJourney(long eventId, PageParams pageParams);

    int getJourneyResponseCount(long id);
}
