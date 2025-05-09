package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UniversityService universityService;
    private final UserDao userDao;
    private final ImageService imageService;
    private final CareerService careerService;
    private final InterestService interestService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UniversityService universityService, UserDao userDao, ImageService imageService, CareerService careerService, InterestService interestService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.universityService = universityService;
        this.userDao = userDao;
        this.imageService = imageService;
        this.careerService = careerService;
        this.interestService = interestService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    @Override
    @Transactional
    public User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, List<String> interests, String password, Locale locale) {
        LOGGER.debug("Creating user for {}", email);

        University university = universityService.findByName(universityName).orElseThrow(() -> new RuntimeException("University not found")); // TODO: ¿Acá cuando tira excepción debería haber un log?

        Career career = careerService.findByName(careerName).orElseThrow(() -> new RuntimeException("Career not found"));

        long profilePictureId = imageService.storeImage(profilePicture);

        String uid = UUID.randomUUID().toString();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        User user = userDao.create(email, username, firstname, lastname, university, career, profilePictureId, passwordEncoder.encode(password), locale,uid,tomorrow);

        interestService.saveUserInterests(interests, user.getId());
        emailService.sendValidationEmail(user,uid);
        return user;
    }

    @Override
    @Transactional
    public void changePassword(String email, String newPassword) {
        LOGGER.debug("Changing password for user {}", email);
        userDao.changePassword(email, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional(readOnly = false)
    public void validateEmail(String token) {
        if (userDao.hasExpired(token)) {
            throw new IllegalStateException("Token expired");
        }
        if(!userDao.isValid(token)){
            throw new IllegalStateException("Token already used");
        }
        userDao.validateToken(token);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<UserPassword> findByEmailWithPass(String email) {
        return userDao.findByEmailWithPass(email);
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
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

    @Override
    @Transactional
    public void updateProfilePicture(long userId, byte[] profilePicture) {
        LOGGER.debug("Updating profile picture for user {}", userId);
        long profilePictureId = imageService.storeImage(profilePicture);
        userDao.updateProfilePicture(userId, profilePictureId);
    }

    @Override
    @Transactional
    public void updateProfileInfo(long userId, String firstname, String lastname, String username) {
        LOGGER.debug("Updating profile info for user {}: firstname={}, lastname={}, username={}", userId, firstname, lastname, username);
        userDao.updateProfileInfo(userId, firstname, lastname, username);
    }

    @Override
    @Transactional
    public void updateLocale(long userId, Locale locale) {
        LOGGER.debug("Updating locale for user {} to {}", userId, locale);
        userDao.updateLocale(userId, locale);
    }

    @Override
    @Transactional
    public void updateUniversity(long userId, String newUniversityName) {
        LOGGER.debug("Updating university for user {} to {}", userId, newUniversityName);
        userDao.updateUniversity(userId, newUniversityName);
    }

    @Override
    @Transactional
    public void updateUniversity(long userId, long universityId) {
        LOGGER.debug("Updating university for user {} to university ID {}", userId, universityId);
        userDao.updateUniversity(userId, universityId);
    }

    @Override
    @Transactional
    public void updateCareer(long userId, String newCareerName) {
        LOGGER.debug("Updating career for user {} to {}", userId, newCareerName);
        userDao.updateCareer(userId, newCareerName);
    }

    @Override
    @Transactional
    public void updateCareer(long userId, long careerId) {
        LOGGER.debug("Updating career for user {} to career ID {}", userId, careerId);
        userDao.updateCareer(userId, careerId);
        LOGGER.info("Successfully updated career for user {} to career ID {}", userId, careerId);
    }


    @Override
    public byte[] getProfilePictureData(User user) {
        return imageService.getImage(user.getProfilePictureId())
                .orElseThrow(() -> new IllegalStateException("User does not have a profile picture"))
                .getData();
    }



    @Override
    public Page<User> getAllUsers(String search, PageParams pageParams) {

        if (search == null || search.isEmpty()) {
            return userDao.getAllUsers(pageParams);
        }
        return userDao.searchUsers(search, pageParams);
    }

    @Override
    @Transactional
    public void blockUser(long userId) {
        emailService.sendUserBlockedNotification(findById(userId).orElseThrow(()-> new IllegalStateException("User does not exist")));

        userDao.blockUser(userId);
    }

    @Override
    @Transactional
    public void unblockUser(long userId) {
        emailService.sendUserUnblockedNotification(findById(userId).orElseThrow(()-> new IllegalStateException("User does not exist")));
        userDao.unblockUser(userId);
    }

}
