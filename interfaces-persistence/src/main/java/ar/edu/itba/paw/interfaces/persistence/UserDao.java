package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserDao {
    User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, String validateToken, LocalDate validateTokenExpiration);

    void updatePasswordByToken(String token, String newPassword);

    Optional<User> findById(long id);

    void updateToken(long id, String uuid, LocalDate date);

    boolean isValidByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<UserAuthInfo> validateEmail(String token);

    Optional<UserAuthInfo> findAuthInfoByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void updatePassword(long id, String password);

    void updateProfilePicture(long id, long profilePictureId);

    void update(long id, String firstname, String lastname, String username, Long universityId, Long careerId, Locale locale);


    void updateBlock(long id, boolean bool);

    boolean isValidated(String token); // todo: rename to findValidatedByToken ?

    Optional<User> findByToken(String token);

    Page<User> findAll(PageParams pageParams);

    Page<User> search(String search, PageParams pageParams);

    boolean isTokenValid(String token); // todo: rename to existsByToken() ?

    boolean hasExpired(String token);

    void validateToken(String token); // todo: renombrar a updateToken o algo así?

    void refreshToken(String newToken, LocalDate date, String oldToken); // todo: idem anterior

    // van en este dao?

    List<User> findAllJourneyResponders(long journeyId);

    List<User> findAllEventResponders(long eventId);

}