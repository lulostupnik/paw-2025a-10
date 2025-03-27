package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    Event create(long userId, long cityId, Date date, String description, long flyerImageId);
    //BUSCO POR CIUDAD,DATE O BUSCO POR QUERY(ciudad + fecha).
    List<Event> listByCity(long cityId);
    List<Event> listByDate(Date date);
    Optional<Event> findByUserId(long userId);
    Optional<Event> findById(long eventId);
}
