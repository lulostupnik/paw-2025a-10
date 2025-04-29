package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UniversityService universityService;
    private final UserDao userDao;
    private final ImageService imageService;
    private final CareerService careerService;
    private final InterestService interestService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UniversityService universityService, UserDao userDao, ImageService imageService, CareerService careerService, InterestService interestService, PasswordEncoder passwordEncoder) {
        this.universityService = universityService;
        this.userDao = userDao;
        this.imageService = imageService;
        this.careerService = careerService;
        this.interestService = interestService;
        this.passwordEncoder = passwordEncoder;
    }

    // ¿debería hacer el @CachePut? -> para eso tendría que resolver el tema de que createUser retorna un User y no un Optional<User>
    @Override
    @Transactional
    public User createUser(String email, String username, String firstname, String lastname, String universityName,
                           String careerName, byte[] profilePicture, String[] interests, String password, Locale locale) {

        LOGGER.debug("Creating user for {}", email);

        LOGGER.debug("Looking for university {}", universityName);
        University university = universityService.findByName(universityName).orElseThrow(() -> new RuntimeException("University not found"));

        LOGGER.debug("Looking for career {}", careerName);
        Career career = careerService.findByName(careerName).orElseThrow(() -> new RuntimeException("Career not found"));

        LOGGER.debug("Saving profile picture");
        long profilePictureId = imageService.storeImage(profilePicture);

        LOGGER.info("User data is valid, commiting new user to persistance", universityName);
        User user = userDao.create(email, username, firstname, lastname, university, career, profilePictureId,
                passwordEncoder.encode(password), locale);

        LOGGER.debug("Saving user interests {}", interests.toString());
        interestService.createUserInterests(interests, user.getId());
        
        LOGGER.info("Successfully created user {}", user);
        return user;
    }



    @Override
    @Cacheable(value = "usersByEmail", key = "#email")
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserPassword> findByEmailWithPass(String email) {
        return userDao.findByEmailWithPass(email);
    }

    @Override
    @Cacheable(value = "usersById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    @Cacheable(value = "usersByUsername", key = "#username")
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersById", "usersByEmail", "usersByUsername"}, key = "#userId")
    public void updateProfilePicture(long userId, byte[] profilePicture) {
        LOGGER.debug("Updating profile picture for user {}", userId);
        long profilePictureId = imageService.storeImage(profilePicture);
        userDao.updateProfilePicture(userId, profilePictureId);
        LOGGER.info("Successfully updated profile picture for user {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersById", "usersByEmail", "usersByUsername"}, key = "#userId")
    public void updateProfileInfo(long userId, String firstname, String lastname, String username) {
        LOGGER.debug("Updating profile info for user {}: firstname={}, lastname={}, username={}",
                userId, firstname, lastname, username);

        // Validate that username is not already taken (if changed)
        Optional<User> existingUser = findById(userId);
        if (existingUser.isPresent() && !existingUser.get().getUsername().equals(username)) {
            if (existsByUsername(username)) {
                LOGGER.warn("Username {} is already taken", username);
                throw new IllegalArgumentException("Username is already taken");
            }
        }

        userDao.updateProfileInfo(userId, firstname, lastname, username);
        LOGGER.info("Successfully updated profile info for user {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersById", "usersByEmail", "usersByUsername"}, key = "#userId")
    public void updateLocale(long userId, Locale locale) {
        LOGGER.debug("Updating locale for user {} to {}", userId, locale);
        userDao.updateLocale(userId, locale);
        LOGGER.info("Successfully updated locale for user {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersById", "usersByEmail", "usersByUsername"}, key = "#userId")
    public void updateUniversity(long userId, String newUniversityName) {
        LOGGER.debug("Updating university for user {} to {}", userId, newUniversityName);

        University university = universityService.findByName(newUniversityName)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found: {}", newUniversityName);
                    return new IllegalArgumentException("University not found");
                });

        userDao.updateUniversity(userId, university.getId());
        LOGGER.info("Successfully updated university for user {} to {}", userId, newUniversityName);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersById", "usersByEmail", "usersByUsername"}, key = "#userId")
    public void updateUniversity(long userId, long universityId) {
        LOGGER.debug("Updating university for user {} to university ID {}", userId, universityId);

        // Validate that university exists
        universityService.findById(universityId)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found with ID: {}", universityId);
                    return new IllegalArgumentException("University not found");
                });

        userDao.updateUniversity(userId, universityId);
        LOGGER.info("Successfully updated university for user {} to university ID {}", userId, universityId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersById", "usersByEmail", "usersByUsername"}, key = "#userId")
    public void updateCareer(long userId, String newCareerName) {
        LOGGER.debug("Updating career for user {} to {}", userId, newCareerName);

        Career career = careerService.findByName(newCareerName)
                .orElseThrow(() -> {
                    LOGGER.warn("Career not found: {}", newCareerName);
                    return new IllegalArgumentException("Career not found");
                });

        userDao.updateCareer(userId, career.getId());
        LOGGER.info("Successfully updated career for user {} to {}", userId, newCareerName);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersById", "usersByEmail", "usersByUsername"}, key = "#userId")
    public void updateCareer(long userId, long careerId) {
        LOGGER.debug("Updating career for user {} to career ID {}", userId, careerId);

        // Validate that career exists
        careerService.findById(careerId)
                .orElseThrow(() -> {
                    LOGGER.warn("Career not found with ID: {}", careerId);
                    return new IllegalArgumentException("Career not found");
                });

        userDao.updateCareer(userId, careerId);
        LOGGER.info("Successfully updated career for user {} to career ID {}", userId, careerId);
    }


    //@TODO ask (exception?). @TODO add cache?
    public byte[] getProfilePictureData(User user) {
        return imageService.getImage(user.getProfilePictureId()).orElseThrow(() -> new IllegalStateException("User does not have a profile picture"))
                .getData();
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        return userDao.getAllUsers(page,size);
    }

    @Override
    public Page<User> searchUsers(String search, int page, int size) {
        return userDao.searchUsers(search, page, size);
    }

}
