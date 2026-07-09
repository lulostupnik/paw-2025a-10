package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import java.time.LocalDate;
import java.util.Optional;

public interface JourneyService {
    Journey createJourney(long userId, long destinationUniversityId, LocalDate startDate, LocalDate endDate, String description);

    Journey updateJourney(long journeyId, long destinationUniversityId, LocalDate startDate, LocalDate endDate, String description);
    void deleteJourney(long id, String message);
    void patchJourney(long id, Boolean deleted, String deletionMessage);

    JourneyResponse createJourneyResponse(long userId, long journeyId, String message);
    void deleteJourneyResponse(long id, String message);
    void deleteJourneyResponse(long journeyId, long responseId, String message);

    Page<Journey> findJourneys(String search, PageParams pageParams);
    Page<Journey> findJourneys(String search, Long excludeUserId, Long destinationCityId, SortFieldJourney sortBy, SortDirection direction, String city, String university, LocalDate startDate, LocalDate endDate, String interest, boolean isPast, boolean isUpcoming, boolean isOngoing, PageParams pageParams);
    Page<Journey> findJourneys(String search, Long recommendedForUser, Long excludeUserId, Long destinationCityId, SortFieldJourney sortBy, SortDirection direction, String city, String university, LocalDate startDate, LocalDate endDate, String interest, boolean isPast, boolean isUpcoming, boolean isOngoing, PageParams pageParams);
    Optional<Journey> findJourneyById(long id);

    boolean existsByUserEmail(String email);

    boolean isJourneyOwnedByUser(String email, long journeyID);
    boolean isJourneyOwnedByUser(Journey journey, User user);

    Optional<JourneyResponse> findJourneyResponseById(long id);

    Optional<JourneyResponse> findJourneyResponseById(long journeyId, long responseId);
    Page<JourneyResponse> findJourneyResponses(long journeyId, PageParams pageParams);

    Page<Tip> findTipsByJourneyId(long journeyId, PageParams pageParams);
    Tip createTip(long journeyId, String title, String content);
    Tip updateTip(long journeyId, long tipId, String title, String content);
    Tip patchTip(long journeyId, long tipId, String title, String content);
    void deleteTip(long journeyId, long tipId);
    Optional<Tip> findTipById(long tipId);
    Optional<Tip> findTipById(long journeyId, long tipId);
    boolean isTipOwnedByUser(long journeyId, long tipId, String email);
}
