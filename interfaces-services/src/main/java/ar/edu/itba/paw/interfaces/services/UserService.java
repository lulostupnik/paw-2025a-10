package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, long universityId, long careerId, List<Long> interestIds, String password, Locale locale);
    void verifyUser(long id);
    void initiatePasswordReset(String email);
    void resendVerificationEmail(String email);
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

    User patchUser(long userId, String username, String firstname, String lastname, Long universityId, Long careerId,
                   String password, Boolean verified, Boolean blocked);

    long updateProfilePicture(long userId, byte[] profilePicture);
    Optional<Image> getProfilePicture(long userId); // TODO: NOTE THAT THIS SHOULD THROW USERNOTFOUNDEXCEPTION IF THE USER IS NOT FOUND.

    Optional<Double> findAverageRatingForCreatedEvents(long userId);
    Optional<Double> findAverageRatingForAttendedEvents(long userId);

    UserRating getUserRating(long userId);
}
