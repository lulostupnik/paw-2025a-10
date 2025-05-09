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

    void updateData(long journeyId, University destinationUniversity, LocalDate startDate, LocalDate endDate, String description);

    void delete(long id);

    void deletionMessage(long id, String message);




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
    Page<Journey> getRecommendedJourneys(String email, PageParams pageParams);
    List<Journey> getOthersJourneys(long userId);


    // PAGE
    Page<Journey> listAll(PageParams pageParams);
    Page<Journey> getOthersJourneys(long userId, PageParams pageParams);
    Page<Journey> findByFilters(Long userId, Long cityId, LocalDate startDate, LocalDate endDate, Long interest, PageParams pageParams);
    Page<Journey> findByOriginCity(long originCityId, PageParams pageParams);
    // Page<Journey> getRecommendedJourneys(Long userId, PageParams pageParams); // fixme
    Page<Journey> searchJourneys(String search, PageParams pageParams);

    Page<Journey> searchJourneys(String search, Long userId, String orderBy, String direction,
                                 Long cityId, LocalDate startDate, LocalDate endDate, Long interest,
                                 boolean isPast, boolean isUpcoming, boolean isMyDestination,
                                 PageParams pageParams);

    // ELIMINAR o reemplazar
    Optional<Journey> findOverlappingJourney(long id, LocalDate startDate, LocalDate endDate);

    List<Journey> getJourneysByUser(String email);

}

