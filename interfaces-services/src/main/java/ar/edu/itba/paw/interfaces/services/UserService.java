package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import java.util.List;
import java.util.Locale;
import ar.edu.itba.paw.models.UserPassword;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, List<String> interests, String password, Locale locale);
    Optional<User> findByEmail(String email);
    Optional<UserPassword> findByEmailWithPass(String email);
    Optional<User> findById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void validateToken(String token);
    Page<User> getAllUsers(String search, PageParams pageParams);
    void blockUser(long userId);
    void unblockUser(long userId);
    void changePassword(String email, String newPassword);
    void validateEmail(String token);
    void refreshToken(String oldToken);
    void refreshPassToken(String oldToken);
    void newPassword(String token, String newPassword);
    void forgotPass(String email);
    }


//     void updateCareer(long userId, String newCareerName);

//     void updateCareer(long userId, long careerId);
//
//void updateUniversity(long userId, String newUniversityName);
//void updateUniversity(long userId, long universityId);

//    byte[] getProfilePictureData(User user);

//     void updateProfileInfo(long userId, String firstname, String lastname, String username);

//    void updateLocale(long userId, Locale locale);

//    void updateProfilePicture(long userId, byte[] profilePicture);

//    Optional<User> findByUsername(String username);
