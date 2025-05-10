package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;

import java.time.LocalDate;
import java.util.Optional;

public interface JourneyDao {
    Journey create(User user, University university, LocalDate startDate, LocalDate endDate, String description);
    void updateDates(long journeyId, LocalDate startDate, LocalDate endDate);
    void updateDescription(long journeyId, String description);
    void updateDestinationUniversity(long journeyId, long universityId);
    void updateData(long journeyId, University destinationUniversity, LocalDate startDate, LocalDate endDate, String description);
    void delete(long id);
    void deletionMessage(long id, String message);
    Optional<Journey> findById(long id);
    Optional<Journey> findByUserId(long userId);
    Optional<Journey> findByUserEmail(String email);
    Page<Journey> getRecommendedJourneys(String email, PageParams pageParams);
    Page<Journey> listAll(PageParams pageParams);
    Page<Journey> getOthersJourneys(long userId, PageParams pageParams);
    Page<Journey> findByFilters(Long userId, Long cityId, LocalDate startDate, LocalDate endDate, Long interest, PageParams pageParams);
    Page<Journey> findByOriginCity(long originCityId, PageParams pageParams);
    Page<Journey> searchJourneys(String search, PageParams pageParams);
    Page<Journey> searchJourneys(String search, Long userId, SortFieldJourney orderBy, SortDirection direction,
                                 String city, LocalDate startDate, LocalDate endDate, String interest,
                                 boolean isPast, boolean isUpcoming, boolean isMyDestination, boolean isOngoing,
                                 PageParams pageParams);
    Optional<Journey> findOverlappingJourney(long id, LocalDate startDate, LocalDate endDate);
}

