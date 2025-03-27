package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {
    User createUser(String email, String username, String firstname, String lastname, String universityName, String career, long profilePictureId);
}
