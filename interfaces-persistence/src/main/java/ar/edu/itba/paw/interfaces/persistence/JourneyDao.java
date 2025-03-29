package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.User;

import java.util.Date; // se podría importar de java.sql.Date
import java.util.List;
import java.util.Optional;

public interface JourneyDao {
    // List<Journey> pageJourneys(int page, int pageSize);

    //SI VAMOS A QUERER FILTRAR VA A HABER QUE BUSCAR POR QUERY, CATEGORIA, FECHA, ETC.
    Journey create(User user, long destinationUniversityId, String destinationCity, Date startDate, Date endDate, String description);
    List<Journey> listAll();
    // Optional<Journey> findByUserId(long userId); // eventualmente podría ser un List<Journey>
}
