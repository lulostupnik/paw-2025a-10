package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
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
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UniversityService universityService, UserDao userDao) {
        this.universityService = universityService;
        this.userDao = userDao;
    }

    public User createUser(String email, String username, String firstname, String lastname, String universityName, String career, long profilePictureId) {
        Optional<University> university = universityService.findByName(universityName);
        if (university.isEmpty()) {
            throw new RuntimeException("University not found");
        }
        return userDao.create(email, username, firstname, lastname, university.get().getId(), career, profilePictureId);
    }

    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
