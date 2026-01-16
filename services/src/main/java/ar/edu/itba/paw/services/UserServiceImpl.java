package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

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
    private final TokenService tokenService;

    @Autowired
    public UserServiceImpl(final UniversityService universityService, final UserDao userDao, final ImageService imageService, final CareerService careerService, final InterestService interestService,final  PasswordEncoder passwordEncoder, final EmailService emailService, final TokenService tokenService) {
        this.universityService = universityService;
        this.userDao = userDao;
        this.imageService = imageService;
        this.careerService = careerService;
        this.interestService = interestService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenService = tokenService;
    }


    @Override
    @Transactional
    public User createUser(final String email,final  String username,final  String firstname, final  String lastname,final  String universityName, final String careerName, final List<String> interests,final  String password, final Locale locale) {
        LOGGER.debug("Creating new user with email: {} and username: {}", email, username);
        University university = universityService.findByName(universityName)
                .orElseThrow(() -> {
                    LOGGER.error("University not found: '{}' during user creation for email: {}", universityName, email);
                    return new InvalidReferenceException("University", universityName);
                });

        Career career = careerService.findCareerByName(careerName)
                .orElseThrow(() -> {
                    LOGGER.error("Career not found: '{}' during user creation for email: {}", careerName, email);
                    return new InvalidReferenceException("Career", careerName);
                });

        User user = userDao.create(email, username, firstname, lastname, university, career, null, passwordEncoder.encode(password), Locale.of(locale.getLanguage()), false);
        LOGGER.info("Successfully created user with ID: {} and email: {}", user.getId(), email);
        interestService.createUserInterests(interests, user.getId());
        LOGGER.info("User interests saved successfully for user ID: {}", user.getId());
        Token token = tokenService.userTokenControl(user);
        emailService.sendValidationEmail(new EmailUser(user), token.getToken());
        LOGGER.info("Validation email sent successfully to user ID: {}", user.getId());
        return user;
    }



    @Transactional
    @Override
    public User verifyUser(String tokenStr) {
        Token token = tokenService.getByToken(tokenStr)
                .orElseThrow(() -> {
                    LOGGER.error("Token is invalid, or expired for token: {}", tokenStr);
                    return new InvalidTokenException(tokenStr);
                });

        final User user = token.getUser();

        tokenService.delete(token);

        if (user.isValidated()) {
            LOGGER.error("User already validated {}", user.getId());
            throw new UserValidatedException();
        }

        user.setValidated(true);
        LOGGER.info("Activating user id {} after successful verification", user.getId());
        return user;
    }


    @Override
    @Transactional
    public void updatePassword(final long id, final String newPassword) {
        User user = userDao.findById(id).orElseThrow(() -> {
            LOGGER.error("User does not exist for ID: {}", id);
            return new UserNotFoundException(id);
        });
        LOGGER.debug("Password change for user with id: {}", id);
        user.setPassword(passwordEncoder.encode(newPassword));
        LOGGER.info("Password changed successfully for user ID: {}", id);

    }


    @Override
    public Optional<User> findUserByEmail(final String email) {
        LOGGER.debug("Searching for user with email: {}", email);
        return userDao.findByEmail(email);
    }


    @Override
    public Optional<User> findUserById(final long id) {
        LOGGER.debug("Searching for user with id: {}", id);
        return userDao.findById(id);
    }


    @Override
    public boolean existsByUsername(final String username) {
        LOGGER.debug("Checking for user existence, with username: {}", username);
        return userDao.existsByUsername(username);
    }


    @Override
    public boolean existsByEmail(final String email) {
        LOGGER.debug("Checking for user existence, with username: {}", email);
        return userDao.existsByEmail(email);
    }


    @Override
    public Page<User> findUsers(final String search, final  PageParams pageParams,  Long attendingEventId,
                                Long universityId,
                                Long careerId,
                                Long interestId,
                                Boolean blocked) {
        LOGGER.debug("Getting all the users with search param: {} and pageParams: {}", search, pageParams);

        return userDao.findUsers(search, pageParams, attendingEventId, universityId, careerId, interestId, blocked);
    }


    @Override
    @Transactional
    public void blockUser(final long userId) {
        LOGGER.debug("Attempting to block user with ID: {}", userId);
        User user = findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User does not exist for ID: {}", userId);
            return new UserNotFoundException(userId);
        });

        emailService.sendUserBlockedNotification(new EmailUser(user));

        user.setBlocked(true);
        LOGGER.info("User blocked successfully with ID: {}", userId);
    }


    @Override
    @Transactional
    public void unblockUser(final long userId) {
        LOGGER.debug("Attempting to unblock user with ID: {}", userId);
        User user = findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User does not exist for ID: {}", userId);
            return new UserNotFoundException(userId);
        });
        emailService.sendUserUnblockedNotification(new EmailUser(user));
        user.setBlocked(false);
        LOGGER.info("User unblocked successfully with ID: {}", userId);
    }




    @Override
    public Optional<Double> findAverageRatingForCreatedEvents(long userId) {
        return userDao.findAverageRatingForCreatedEvents(userId);
    }

    @Override
    public Optional<Double> findAverageRatingForAttendedEvents(long userId) {
        return userDao.findAverageRatingForAttendedEvents(userId);
    }


    @Override
    @Transactional
    public void resetPassword(final String token, final String newPassword) {
        final Optional<Token> maybeToken = tokenService.getByToken(token);
        if (maybeToken.isEmpty()) {
            LOGGER.error("Token is invalid, or expired for token: {}", token);
            throw new InvalidTokenException(token);
        }

        final Token tkn = maybeToken.get();
        final User user = tkn.getUser();

        tokenService.delete(tkn);
        LOGGER.debug("updating new password for token: {}", token);
        user.setPassword(passwordEncoder.encode(newPassword));
        LOGGER.info("Password updated successfully for token: {}", token);
    }


    @Override
    @Transactional
    public void initiatePasswordReset(final String email) {
        LOGGER.debug("Attempting to send forgot password email to: {}", email);
        User user = userDao.findByEmail(email).orElseThrow(()-> {
            LOGGER.error("User with email {} not found", email);
            return new UserNotFoundException(email);
        });
        if(!user.isValidated()){
            LOGGER.warn("User with email {} not validated", email);
            throw new UserValidatedException(email);
        }
        Token token = tokenService.userTokenControl(user);
        emailService.sendForgotPassEmail(new EmailUser(user), token.getToken());
        LOGGER.info("Forgot password email sent successfully to: {}", email);
    }

    @Override
    @Transactional
    public User updateUser(final long userId, final String username,
                           final String firstname, final String lastname, final String universityName,
                           final String careerName) {
        LOGGER.debug("Updating user with ID: {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        University university = universityService.findByName(universityName)
                .orElseThrow(() -> {
                    LOGGER.error("University not found: '{}' during user update for user ID: {}", universityName, userId);
                    return new InvalidReferenceException("University", universityName);
                });

        Career career = careerService.findCareerByName(careerName)
                .orElseThrow(() -> {
                    LOGGER.error("Career not found: '{}' during user update for user ID: {}", careerName, userId);
                    return new InvalidReferenceException("Career", careerName);
                });

        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUniversity(university);
        user.setCareer(career);

        LOGGER.info("User updated successfully with ID: {}", userId);
        return user;
    }

    @Override
    @Transactional
    public long updateProfilePicture(final long userId, final byte[] profilePicture) {
        LOGGER.debug("Updating profile picture for user ID: {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        long newProfilePictureId = imageService.createImage(profilePicture);

        user.setProfilePictureId(newProfilePictureId);

        LOGGER.info("Profile picture updated successfully for user ID: {}", userId);
        return newProfilePictureId;
    }

    @Override
    public Optional<Image> getProfilePicture(final long userId) {
        LOGGER.debug("Getting profile picture for user ID: {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        if (user.getProfilePictureId() == null) {
            return Optional.empty();
        }

        return imageService.findImage(user.getProfilePictureId());
    }
}
