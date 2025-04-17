package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password);
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<UserPassword> findByEmailWithPass(String email);

    Optional<User> findByUsername(String username);
    void changePassword(String email, String password);

    // podríamos generalizar en findBy(String field, String value) o algo por el estilo

}
