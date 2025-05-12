package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import java.time.LocalDate;
import java.util.Optional;

public interface JourneyDao {
    Journey create(User user, University university, LocalDate startDate, LocalDate endDate, String description);
    void update(long journeyId, University destinationUniversity, LocalDate startDate, LocalDate endDate, String description);
    void delete(long id);
    void updateDeletionMessage(long id, String message);
    Optional<Journey> findById(long id);
    Optional<Journey> findByUserId(long userId);
    Page<Journey> findRecommended(String email, PageParams pageParams);
    Page<Journey> findAll(PageParams pageParams);
    Page<Journey> findByOriginCity(long originCityId, PageParams pageParams);
    Page<Journey> search(String search, PageParams pageParams);
    Page<Journey> search(String search, Long userId, SortFieldJourney orderBy, SortDirection direction,
                         String city, LocalDate startDate, LocalDate endDate, String interest,
                         boolean isPast, boolean isUpcoming, boolean isMyDestination, boolean isOngoing,
                         PageParams pageParams);
}
