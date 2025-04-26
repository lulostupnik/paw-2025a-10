package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JourneyDao {

    // CREATE
    Journey create(User user, University university, LocalDate startDate, LocalDate endDate, String description);


    // UPDATE
    void updateDates(long journeyId, LocalDate startDate, LocalDate endDate);
    void updateDescription(long journeyId, String description);
    void updateDestinationUniversity(long journeyId, long universityId);


    // GET (ONE)
    Optional<Journey> findById(long id);
    Optional<Journey> findByUserId(long userId);
    Optional<Journey> findByUserEmail(String email);


    // LIST
    List<Journey> listAll();
    List<Journey> findByFilters(String destination, LocalDate startDate, LocalDate endDate, String interest);
    List<Journey> findByFilters(long userId, String destination, LocalDate startDate, LocalDate endDate, String interest);
    List<Journey> findByOriginCity(long originCityId);
    List<Journey> findByOriginUniversity(long originUniversityId);
    List<Journey> getRecommendedJourneys(String email);
    List<Journey> getOthersJourneys(long userId);


    // PAGE
    Page<Journey> listAll(int page, int size);
    Page<Journey> getOthersJourneys(long userId, int page, int size);
    Page<Journey> findByFilters(Long userId, Long cityId, LocalDate startDate, LocalDate endDate, Long interest, int page, int size);
    Page<Journey> findByOriginCity(long originCityId, int page, int size);
    // Page<Journey> getRecommendedJourneys(Long userId, int page, int size);


    // ELIMINAR o reemplazar
    Optional<Journey> findOverlappingJourney(long id, LocalDate startDate, LocalDate endDate);
    List<Journey> getJourneysByUser(String email);

    void delete(long id);
}

