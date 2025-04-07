package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, String[] interests);

    Optional<User> findByEmail(String email);
}
