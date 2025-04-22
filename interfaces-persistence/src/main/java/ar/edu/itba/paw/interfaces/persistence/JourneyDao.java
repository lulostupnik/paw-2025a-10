package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JourneyDao {

    Journey create(User user, University university, LocalDate startDate, LocalDate endDate, String description);
    List<Journey> listAll();
    CursorPage<Journey, Long> listAll(Long cursor, int pageSize);

    Optional<Journey> findById(long id);
    Optional<Journey> findOverlappingJourney(long id, LocalDate startDate, LocalDate endDate);
    List<Journey> findByFilters(String destination, LocalDate startDate, LocalDate endDate, String interest);
    CursorPage<Journey, Long> findByFilters(String destination, LocalDate startDate, LocalDate endDate, String interest, Long cursor, int pageSize);

    List<Journey> findByOriginCity(long originCityId);
    List<Journey> findByOriginUniversity(long originUniversityId);

    Optional<Journey> findByUserId(long userId);
    List<Journey> getRecommendedJourneys(String email);
    List<Journey> getJourneysByUser(String email);
    List<Journey> getOthersJourneys(long userId);
    void updateDates(long journeyId, LocalDate startDate, LocalDate endDate);
    void updateDescription(long journeyId, String description);
    void updateDestinationUniversity(long journeyId, long universityId);
}

    // CursorPage<Journey, Long> getRecommendedJourneys(String email, Long cursor, int pageSize);
