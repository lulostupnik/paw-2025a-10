package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import java.time.LocalDate;
import java.util.Optional;

public interface JourneyDao {
    Journey create(User user, University university, LocalDate startDate, LocalDate endDate, String description);

    void hardDelete(Journey journey);

    Optional<Journey> findById(long id);
    Page<Journey> findRecommended(String email, PageParams pageParams);
    Page<Journey> findAll(PageParams pageParams);
    Page<Journey> findByOriginCity(long originCityId, PageParams pageParams);
    Page<Journey> search(String search, Long userId, SortFieldJourney orderBy, SortDirection direction,
                         String city, LocalDate startDate, LocalDate endDate, String interest,
                         boolean isMyDestination, PageParams pageParams);
}
