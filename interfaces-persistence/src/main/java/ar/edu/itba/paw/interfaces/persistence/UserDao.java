package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.util.Locale;
import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, boolean validated);


    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findAll(PageParams pageParams);

    Page<User> search(String search, PageParams pageParams);

    Optional<Double> findAverageRatingForCreatedEvents(long userId);
    Optional<Double> findAverageRatingForAttendedEvents(long userId);
}
