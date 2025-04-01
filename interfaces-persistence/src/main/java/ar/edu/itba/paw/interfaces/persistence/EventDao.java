package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    Event create(User user, City city, Date date, String description, long flyerImageId);
    List<Event> listByQuery(Long cityId, Date date);
    //Optional<Event> findByUserId(long userId);
    //Optional<Event> findById(long eventId);
}
