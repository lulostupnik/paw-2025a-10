package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, String validateToken, LocalDate validateTokenExpiration);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    Optional<UserAuthInfo> findByEmailWithPass(String email);
    
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void changePassword(String email, String password);

    void updateProfileInfo(long userId, String firstname, String lastname, String username);

    void updateLocale(long userId, Locale locale);

    void updateProfilePicture(long userId, long profilePictureId);

    void update(long userId, String firstname, String lastname, String username, Long universityId, Long careerId, Locale locale);

    void blockUser(long userId);

    void unblockUser(long userId);

    Optional<User> getUserByToken(String token);

    List<User> listJourneyRespondersMinusUsers(long journeyId/*, List<Long> userIds*/);

    List<User> listEventRespondersMinusUsers(long eventId/*, List<Long> userIds*/);

    List<User> getAllUsers();

    Page<User> getAllUsers(PageParams pageParams);

    Page<User> searchUsers(String search, PageParams pageParams);

    // podríamos generalizar en findBy(String field, String value) o algo por el estilo
    boolean isValid(String token);

    boolean hasExpired(String token);

    void validateToken(String token);

    void refreshToken(String newToken, LocalDate date, String oldToken);
}

//     void updateCareer(long userId, String careerName);

//     void updateCareer(long userId, long careerId);

//
//    void updateUniversity(long userId, long universityId);
//
//    void updateUniversity(long userId, String universityName);
//
