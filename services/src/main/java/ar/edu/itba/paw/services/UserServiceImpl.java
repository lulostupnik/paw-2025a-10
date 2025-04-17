package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;

import ar.edu.itba.paw.models.UserPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UniversityService universityService;
    private final UserDao userDao;
    private final ImageDao imageDao;
    private final CareerService careerService;
    private final InterestService interestService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UniversityService universityService, UserDao userDao, ImageDao imageDao, CareerService careerService, InterestService interestService, PasswordEncoder passwordEncoder) {
        this.universityService = universityService;
        this.userDao = userDao;
        this.imageDao = imageDao;
        this.careerService = careerService;
        this.interestService = interestService;
        this.passwordEncoder = passwordEncoder;
    }


    // creo que debería ser @Transactional
    @Override
    public User createUser(String email, String username, String firstname, String lastname, String universityName,
                           String careerName, byte[] profilePicture, String[] interests, String password, Locale locale) {

        LOGGER.debug("Creating user for {}", email);

        LOGGER.debug("Looking for university {}", universityName);
        University university = universityService.findByName(universityName).orElseThrow(() -> new RuntimeException("University not found"));

        LOGGER.debug("Looking for career {}", careerName);
        Career career = careerService.findByName(careerName).orElseThrow(() -> new RuntimeException("Career not found"));

        LOGGER.debug("Saving profile picture");
        long profilePictureId = imageDao.saveImage(profilePicture);

        LOGGER.info("User data is valid, commiting new user to persistance", universityName);
        User user = userDao.create(email, username, firstname, lastname, university, career, profilePictureId, passwordEncoder.encode(password), locale);

        LOGGER.debug("Saving user interests {}", interests.toString());
        interestService.createUserInterests(interests, user.getId());
        
        return user;
    }


    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<UserPassword> findByEmailWithPass(String email) {
        return userDao.findByEmailWithPass(email);
    }

    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }
}
