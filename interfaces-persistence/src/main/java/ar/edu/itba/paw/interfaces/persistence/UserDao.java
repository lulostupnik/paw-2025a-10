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

    boolean findValidationStatusByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<UserAuthInfo> updateValidationAndFindAuthInfoByToken(String token);

    Optional<UserAuthInfo> findAuthInfoByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void updatePassword(long id, String password);

    void updateBlock(long id, boolean bool);

    boolean findValidatedByTokenNotExpired(String token);

    Optional<User> findByToken(String token);

    Page<User> findAll(PageParams pageParams);

    Page<User> search(String search, PageParams pageParams);

    boolean existsByTokenNotExpired(String token);

    boolean existsByTokenExpired(String token);

    void updateTokenAndExpirationByToken(String newToken, LocalDate date, String oldToken); // todo: idem anterior

    List<User> findAllJourneyResponders(long journeyId);

    List<User> findAllEventResponders(long eventId);

}

//    void updateProfilePicture(long id, long profilePictureId);

//    void update(long id, String firstname, String lastname, String username, Long universityId, Long careerId, Locale locale);

//    void clearTokenByToken(String token);
