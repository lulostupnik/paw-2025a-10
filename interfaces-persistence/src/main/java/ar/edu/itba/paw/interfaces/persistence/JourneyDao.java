package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JourneyDao {
    // List<Journey> pageJourneys(int page, int pageSize);

    //SI VAMOS A QUERER FILTRAR VA A HABER QUE BUSCAR POR QUERY, CATEGORIA, FECHA, ETC.
    Journey create(User user, University university, String destinationCity, LocalDate startDate, LocalDate endDate, String description);
    List<Journey> listAll();
    Optional<Journey> findById(long id);
    // Optional<Journey> findByUserId(long userId); // eventualmente podría ser un List<Journey>
    Optional<Journey> findOverlappingJourney(long id, LocalDate startDate, LocalDate endDate);
}
