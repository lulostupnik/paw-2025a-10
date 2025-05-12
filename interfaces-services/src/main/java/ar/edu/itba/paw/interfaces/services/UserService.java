package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import java.util.List;
import java.util.Locale;
import ar.edu.itba.paw.models.UserAuthInfo;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, List<String> interests, String password, Locale locale);

    void updatePassword(long id, String newPassword);
    void resetPassword(String token, String newPassword);
    void initiatePasswordReset(String email);


    Optional<User> findUserByEmail(String email);
    Optional<UserAuthInfo> findAuthInfoByEmail(String email);
    Optional<User> findUserById(long id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Page<User> findUsers(String search, PageParams pageParams);
    List<User> findEventAttendees(long eventId);
    Page<User> findEventAttendees(long eventId, PageParams pageParams);

    void blockUser(long userId);
    void unblockUser(long userId);

    UserAuthInfo verifyEmailToken(String token);
    void checkPasswordTokenValidity(String token);

    void refreshToken(String oldToken);
    void refreshPasswordToken(String oldToken);

}
