package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Locale;
import ar.edu.itba.paw.models.UserPassword;


import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, String[] interests, String password, Locale locale);

    Optional<User> findByEmail(String email);
    Optional<UserPassword> findByEmailWithPass(String email);
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    void updateProfilePicture(long userId, byte[] profilePicture);

    void updateProfileInfo(long userId, String firstname, String lastname, String username);

    void updateLocale(long userId, Locale locale);

    void updateUniversity(long userId, String newUniversityName);
    void updateUniversity(long userId, long universityId);

    void updateCareer(long userId, String newCareerName);
    void updateCareer(long userId, long careerId);
    byte[] getProfilePictureData(User user);

    List<User> getAllUsers();
    Page<User> getAllUsers(int page, int size);
    Page<User> searchUsers(String search, int page, int size);
}