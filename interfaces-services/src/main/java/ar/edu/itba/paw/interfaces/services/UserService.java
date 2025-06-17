package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, List<String> interests, String password, Locale locale);
    User verifyUser(String token);
    void updatePassword(long id, String newPassword);
    void resetPassword(String token, String newPassword);
    void initiatePasswordReset(String email);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findUsers(String search, PageParams pageParams);
    void blockUser(long userId);
    void unblockUser(long userId);

    void updateUser(long userId, String username, String firstname, String lastname,
                    String universityName, String careerName);

    void updateProfilePicture(long userId, byte[] profilePicture);

    Optional<Double> findAverageRatingForCreatedEvents(long userId);
    Optional<Double> findAverageRatingForAttendedEvents(long userId);
}
