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

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    void changePassword(String email, String password);
    void updateProfileInfo(long userId, String firstname, String lastname, String username);
    void updateLocale(long userId, Locale locale);
    void updateUniversity(long userId, long universityId);
    void updateUniversity(long userId, String universityName);
    void updateCareer(long userId, long careerId);
    void updateCareer(long userId, String careerName);
    void updateProfilePicture(long userId, long profilePictureId);
    void update(long userId, String firstname, String lastname, String username, Long universityId, Long careerId, Locale locale);

    void blockUser(long userId);
    void unblockUser(long userId);

    List<User> listJourneyRespondersMinusUsers(long journeyId/*, List<Long> userIds*/);
    List<User> listEventRespondersMinusUsers(long eventId/*, List<Long> userIds*/);

    List<User> getAllUsers();

    Page<User> getAllUsers(int page, int size);
    Page<User> searchUsers(String search, int page, int size);
    // podríamos generalizar en findBy(String field, String value) o algo por el estilo

}
