package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JourneyService {
    Journey createJourney(User user, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description);

    Journey updateJourney(long journeyId, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description);
    void deleteJourney(long id, String message);


    JourneyResponse createJourneyResponse(String email, long journeyId, String message);
    void deleteJourneyResponse(long id, String message);

    Page<Journey> findJourneys(String search, PageParams pageParams);
    Page<Journey> findJourneys(String search, User user, SortFieldJourney sortBy, SortDirection direction, String destination, LocalDate startDate, LocalDate endDate, String interest, boolean isPast, boolean isUpcoming, boolean isMyDestination, boolean isOngoing, PageParams pageParams);
    Optional<Journey> getJourneyById(long id);
    Optional<Journey> getJourneyByEmail(String email);

    boolean existsByUserEmail(String email);
    boolean existsByUser(User user);

    List<Journey> findRecommendedJourneys(String email, int limit);

    boolean isJourneyOwnedByUser(String email, long journeyID);
    boolean isJourneyOwnedByUser(Journey journey, User user);


    Optional<JourneyResponse> findJourneyResponseById(long id);
    Page<JourneyResponse> findJourneyResponses(long eventId, PageParams pageParams);
    int countJourneyResponses(long id);

    Page<Tip> findTipsByJourney(Journey journey, PageParams pageParams);
    Tip createTip(long journeyId, String title, String content);
    Tip updateTip(long tipId, String title, String content);
    void deleteTip(long tipId);
    Optional<Tip> findTipById(long tipId);
    boolean isTipOwnedByUser(long tipId, String email);
}



