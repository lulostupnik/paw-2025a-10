package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale);
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<UserPassword> findByEmailWithPass(String email);

    Optional<User> findByUsername(String username);
    void changePassword(String email, String password);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Basic profile information
    void updateProfileInfo(long userId, String firstname, String lastname, String username);

    // Language preference
    void updateLocale(long userId, Locale locale);

    // Academic affiliations
    void updateUniversity(long userId, long universityId);
    void updateCareer(long userId, long careerId);

    // Profile picture
    void updateProfilePicture(long userId, long profilePictureId);

    void update(long userId, String firstname, String lastname, String username, Long universityId, Long careerId, Locale locale);

    List<User> getAllUsers();

    // podríamos generalizar en findBy(String field, String value) o algo por el estilo

}
