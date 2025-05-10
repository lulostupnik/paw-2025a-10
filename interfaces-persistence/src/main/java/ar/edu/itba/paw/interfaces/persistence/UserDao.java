package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, String validateToken, LocalDate validateTokenExpiration);

    void newPassword(String token, String newPassword); // todo: o en servicios verificar token y después hacer changePassword ?

    Optional<User> findById(long id);

    void updateToken(long id, String uuid, LocalDate date);

    boolean isUserValidByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<UserAuthInfo> validateEmail(String token);

    Optional<UserAuthInfo> findAuthInfoByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void updatePassword(long id, String password);

    void updateProfilePicture(long id, long profilePictureId);

    void update(long id, String firstname, String lastname, String username, Long universityId, Long careerId, Locale locale);

    void blockUser(long id);

    void unblockUser(long id);

    boolean isUserValidated(String token);

    Optional<User> findByToken(String token);

    List<User> listJourneyResponders(long journeyId/*, List<Long> userIds*/);

    List<User> listEventResponders(long eventId/*, List<Long> userIds*/);

    Page<User> findAll(PageParams pageParams);

    Page<User> search(String search, PageParams pageParams);

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

//
//    void updateProfileInfo(long userId, String firstname, String lastname, String username);
//
//    void updateLocale(long userId, Locale locale);
