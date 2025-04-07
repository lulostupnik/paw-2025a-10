package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // dejar de usar esta
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, long profilePictureId);

    // usar esta
    User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, String[] interests);

    Optional<User> findByEmail(String email);
}
