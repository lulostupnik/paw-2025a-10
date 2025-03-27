package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, long universityId, String career);
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // podríamos generalizar en findBy(String field, String value) o algo por el estilo

}
