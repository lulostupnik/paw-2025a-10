package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId);
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // podríamos generalizar en findBy(String field, String value) o algo por el estilo

}
