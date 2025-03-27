package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UniversityService universityService;

    @Autowired
    public UserServiceImpl(UniversityService universityService) {
        this.universityService = universityService;
    }

    public User createUser(String email, String username, String firstname, String lastname, String universityName, String career, long profilePictureId) {
        Optional<University> university = universityService.findByName(universityName);
        if (university.isEmpty()) {
            throw new RuntimeException("University not found");
        }
        return new User(1, email, username, firstname, lastname, university.get(), career, profilePictureId);
    }
}
