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
    Optional<User> findByEmail(String email);
    Optional<UserAuthInfo> findByEmailWithPass(String email);
    Optional<User> findById(long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> getAllUsers(String search, PageParams pageParams);
    void blockUser(long userId);
    void unblockUser(long userId);
    void changePassword(long id, String newPassword);
    UserAuthInfo validateEmail(String token);
    void checkPasswordTokenValidity(String token);
    void refreshToken(String oldToken);
    void refreshPassToken(String oldToken);
    boolean isValidPasswordResetToken(String token);
    void newPassword(String token, String newPassword);
    void forgotPass(String email);
    boolean isTokenExpired(String token);
    }

