package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

import java.util.Locale;
import ar.edu.itba.paw.models.UserPassword;


import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, String[] interests, String password, Locale locale);

    Optional<User> findByEmail(String email);
    Optional<UserPassword> findByEmailWithPass(String email);
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
}
