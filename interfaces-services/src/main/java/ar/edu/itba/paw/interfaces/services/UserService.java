package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, List<String> interests, String password, Locale locale);
    User verifyUser(long id, String token);
    void updatePassword(long id, String newPassword);
    void resetPassword(long id, String token, String newPassword);
    void initiatePasswordReset(String email);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findUsers(String search, PageParams pageParams, Long attendingEventId,
                         Long universityId,
                         Long careerId,
                         Long interestId,
                         Boolean blocked);

    void blockUser(long userId);
    void unblockUser(long userId);
    void setBlockedStatus(long userId, boolean blocked);

    User updateUser(long userId, String username, String firstname, String lastname,
                    String universityName, String careerName);

    User patchUser(long userId, String username, String firstname, String lastname, String universityName, String careerName);

    long updateProfilePicture(long userId, byte[] profilePicture);
    Optional<Image> getProfilePicture(long userId); // TODO: NOTE THAT THIS SHOULD THROW USERNOTFOUNDEXCEPTION IF THE USER IS NOT FOUND.

    Optional<Double> findAverageRatingForCreatedEvents(long userId);
    Optional<Double> findAverageRatingForAttendedEvents(long userId);

    UserRating getUserRating(long userId);
}
