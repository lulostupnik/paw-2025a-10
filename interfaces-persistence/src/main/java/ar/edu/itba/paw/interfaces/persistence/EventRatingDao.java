package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Event;

import java.util.Optional;

public interface EventRatingDao {
    Rating rateEvent(User user, Event event, double rating);

    Optional<Rating> findRatingByUserAndEvent(long userId, long eventId);

    int countRatingsByEvent(long eventId);

}
