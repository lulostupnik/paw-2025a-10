package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Journey;

import java.util.Date; // se podría importar de java.sql.Date
import java.util.List;
import java.util.Optional;

public interface JourneyDao {
    // List<Journey> pageJourneys(int page, int pageSize);

    Journey create(long userId, long destinationUniversityId, String destinationCity, Date startDate, Date endDate,String description);
    List<Journey> listAll();
    // Optional<Journey> findByUserId(long userId); // eventualmente podría ser un List<Journey>
}
