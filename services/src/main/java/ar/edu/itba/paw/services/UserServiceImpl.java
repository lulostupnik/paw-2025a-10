package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UniversityService universityService;
    private final UserDao userDao;
    private final ImageDao imageDao;
    private final CareerService careerService;
    private final InterestService interestService;

    @Autowired
    public UserServiceImpl(UniversityService universityService, UserDao userDao, ImageDao imageDao, CareerService careerService, InterestService interestService) {
        this.universityService = universityService;
        this.userDao = userDao;
        this.imageDao = imageDao;
        this.careerService = careerService;
        this.interestService = interestService;
    }


    // creo que debería ser @Transactional
    @Override
    public User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, String[] interests) {

        University university = universityService.findByName(universityName).orElseThrow(() -> new RuntimeException("University not found"));
        Career career = careerService.findByName(careerName).orElseThrow(() -> new RuntimeException("Career not found"));
        long profilePictureId = imageDao.saveImage(profilePicture);
        User user = userDao.create(email, username, firstname, lastname, university, career, profilePictureId);
        interestService.createUserInterests(interests, user.getId());
        return user;
    }


    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
