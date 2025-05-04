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

    Page<Journey> getAllJourneys(String search, int page, int size);

    Optional<Journey> getJourneyById(long id);

    Optional<Journey> getJourneyByEmail(String email);

    List<Journey> getFilteredJourneys(String destination, LocalDate startDate, LocalDate endDate, String interest);

    List<Journey> getFilteredJourneys(String email, String destination, LocalDate startDate, LocalDate endDate, String interest);

    Page<Journey> getFilteredJourneys(Long destination, LocalDate startDate, LocalDate endDate, Long interest, int page, int size);

    Page<Journey> getFilteredJourneys(String email, Long destination, LocalDate startDate, LocalDate endDate, Long interest, int page, int size);

    Boolean userHasJourney(String email); // ja

    List<Journey> getRecommendedJourneys(String email);

    List<Journey> getJourneysByUser(String email);

    List<JourneyResponse> getJourneyResponses(long journeyId);

    List<Journey> getOthersJourneys(long userId);

    List<Journey> getOthersJourneys(String email);

    void updateJourneyDates(long journeyId, LocalDate startDate, LocalDate endDate);

    void updateJourneyDescription(long journeyId, String description);

    void updateJourneyDestination(long journeyId, String universityName);

    void updateJourneyDestination(long journeyId, long universityId);

    void delete(long id, String message);

    boolean isJourneyOwnedByUser(String email, long journeyID);

    void editJourney(long journeyId, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description);

}
