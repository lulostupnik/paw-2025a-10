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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    public User createUser(final String email,final  String username,final  String firstname, final  String lastname,final  long universityId, final long careerId, final List<Long> interestIds,final  String password, final Locale locale) {
        LOGGER.debug("Creating new user with email: {} and username: {}", email, username);
        University university = universityService.findById(universityId)
                .orElseThrow(() -> {
                    LOGGER.error("University not found: '{}' during user creation for email: {}", universityId, email);
                    return new InvalidReferenceException("University", universityId);
                });

        Career career = careerService.findCareerById(careerId)
                .orElseThrow(() -> {
                    LOGGER.error("Career not found: '{}' during user creation for email: {}", careerId, email);
                    return new InvalidReferenceException("Career", careerId);
                });

        User user = userDao.create(email, username, firstname, lastname, university, career, null, passwordEncoder.encode(password), Locale.of(locale.getLanguage()), false);
        LOGGER.info("Successfully created user with ID: {} and email: {}", user.getId(), email);
        final List<Long> uniqueInterestIds = interestIds == null
                ? List.of()
                : interestIds.stream().filter(Objects::nonNull).distinct().toList();
        interestService.createUserInterests(uniqueInterestIds, user.getId());
        LOGGER.info("User interests saved successfully for user ID: {}", user.getId());
        String rawToken = tokenService.userTokenControl(user);
        runAfterCommit(() -> {
            emailService.sendValidationEmail(new EmailUser(user), rawToken);
            LOGGER.info("Validation email sent successfully to user ID: {}", user.getId());
        });
        return user;
    }

    private void runAfterCommit(final Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }



    @Transactional
    @Override
    public void verifyUser(final long id) {
        final User user = userDao.findById(id).orElseThrow(() -> {
            LOGGER.error("User does not exist for ID: {}", id);
            return new UserNotFoundException(id);
        });

        if (!user.isValidated()) {
            user.setValidated(true);
            LOGGER.info("Activating user id {} after successful verification", id);
        }
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

        user.setBlocked(true);
        runAfterCommit(() -> emailService.sendUserBlockedNotification(new EmailUser(user)));
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
        user.setBlocked(false);
        runAfterCommit(() -> emailService.sendUserUnblockedNotification(new EmailUser(user)));
        LOGGER.info("User unblocked successfully with ID: {}", userId);
    }

    @Override
    @Transactional
    public void setBlockedStatus(final long userId, final boolean blocked) {
        if (blocked) {
            blockUser(userId);
        } else {
            unblockUser(userId);
        }
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
    public UserRating getUserRating(long userId) {
        userDao.findById(userId).orElseThrow(() -> {
            LOGGER.error("User does not exist for ID: {}", userId);
            return new UserNotFoundException(userId);
        });
        final Double createdEventsRating = findAverageRatingForCreatedEvents(userId).orElse(null);
        final Double attendedEventsRating = findAverageRatingForAttendedEvents(userId).orElse(null);
        return new UserRating(userId, createdEventsRating, attendedEventsRating);
    }


    @Override
    @Transactional
    public void initiatePasswordReset(final String email) {
        LOGGER.debug("Attempting to send forgot password email to: {}", email);
        // Siempre responde lo mismo, no revela si el mail existe o no.
        Optional<User> maybeUser = userDao.findByEmail(email);
        if (maybeUser.isEmpty()) {
            LOGGER.info("Ignored password reset request for unknown email");
            return;
        }
        User user = maybeUser.get();
        if (!user.isValidated()) {
            LOGGER.info("Ignored password reset request for non-validated user");
            return;
        }

        String rawToken = tokenService.userTokenControl(user);
        runAfterCommit(() -> emailService.sendForgotPassEmail(new EmailUser(user), rawToken));
        LOGGER.info("Forgot password email sent successfully to: {}", email);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(final String email) {
        LOGGER.debug("Attempting to resend verification email to: {}", email);
        final User user = userDao.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        if (user.isValidated()) {
            LOGGER.warn("User with email {} is already validated; skipping verification resend", email);
            throw new UserValidatedException(email);
        }
        String rawToken = tokenService.userTokenControl(user);
        emailService.sendValidationEmail(new EmailUser(user), rawToken);
        LOGGER.info("Verification email resent successfully to: {}", email);
    }

    @Override
    @Transactional
    public User patchUser(final long userId, final String username,
                          final String firstname, final String lastname,
                          final Long universityId, final Long careerId,
                          final String password, final Boolean blocked) {
        LOGGER.debug("Patching user with ID: {}", userId);

        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (username != null) {
            user.setUsername(username);
        }

        if (firstname != null) {
            user.setFirstname(firstname);
        }

        if (lastname != null) {
            user.setLastname(lastname);
        }

        if (universityId != null) {
            University university = universityService.findById(universityId).orElseThrow(() -> new InvalidReferenceException("University", universityId));
            user.setUniversity(university);
        }

        if (careerId != null) {
            Career career = careerService.findCareerById(careerId).orElseThrow(() -> new InvalidReferenceException("Career", careerId));
            user.setCareer(career);
        }

        if (password != null) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (blocked != null) {
            // Reuses the block/unblock flow so the user still gets the notification email.
            setBlockedStatus(userId, blocked);
        }

        LOGGER.info("User patched successfully with ID: {}", userId);
        return user;
    }

    // todo: ¿esta bien retornar la imagen? ¿o devolvemos a que retorne long/void?
    @Override
    @Transactional
    public Image updateProfilePicture(final long userId, final byte[] profilePicture) {
        LOGGER.debug("Updating profile picture for user ID: {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        final Long oldProfilePictureId = user.getProfilePictureId();
        long newProfilePictureId = imageService.createImage(profilePicture);
        user.setProfilePictureId(newProfilePictureId);
        if (oldProfilePictureId != null) {
            imageService.deleteImage(oldProfilePictureId);
            LOGGER.info("Old profile picture {} deleted for user {}", oldProfilePictureId, userId);
        }

        LOGGER.info("Profile picture updated successfully for user ID: {}", userId);
        return new Image(newProfilePictureId, profilePicture);
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
