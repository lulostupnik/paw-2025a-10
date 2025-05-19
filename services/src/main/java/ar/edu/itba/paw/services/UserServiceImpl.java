package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.ExpiredTokenException;
import ar.edu.itba.paw.models.exceptions.InvalidTokenException;
import ar.edu.itba.paw.models.exceptions.UserValidatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
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

    @Autowired
    public UserServiceImpl(final UniversityService universityService, final UserDao userDao, final ImageService imageService, final CareerService careerService, final InterestService interestService,final  PasswordEncoder passwordEncoder, final EmailService emailService) {
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
    public User createUser(final String email,final  String username,final  String firstname, final  String lastname,final  String universityName, final String careerName,final  byte[] profilePicture, final List<String> interests,final  String password, final Locale locale) {
        LOGGER.debug("Creating new user with email: {} and username: {}", email, username);
        University university = universityService.findByName(universityName)
                .orElseThrow(() -> {
                    LOGGER.error("University not found: '{}' during user creation for email: {}", universityName, email);
                    return new RuntimeException("University not found");
                });

        Career career = careerService.findCareerByName(careerName)
                .orElseThrow(() -> {
                    LOGGER.error("Career not found: '{}' during user creation for email: {}", careerName, email);
                    return new RuntimeException("Career not found");
                });

        long profilePictureId = imageService.createImage(profilePicture);
        String uid = UUID.randomUUID().toString();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        User user = userDao.create(email, username, firstname, lastname, university, career, profilePictureId, passwordEncoder.encode(password), locale,uid,tomorrow);
        LOGGER.info("Successfully created user with ID: {} and email: {}", user.getId(), email);
        interestService.createUserInterests(interests, user.getId());
        LOGGER.info("User interests saved successfully for user ID: {}", user.getId());
        emailService.sendValidationEmail(user,uid);
        LOGGER.info("Validation email sent successfully to user ID: {}", user.getId());
        return user;
    }

    @Override
    @Transactional
    public void updateUserInterestScores(List<Interest> interests, User user){
        HashSet<Long> interestIds = new HashSet<>(interests.size());
        for (Interest interest : interests) {
            interestIds.add(interest.getId());
        }

        user.getInterests().forEach(userInterest -> {
            if (interestIds.contains(userInterest.getInterest().getId())) {
                userInterest.setScore(userInterest.getScore() + 1);
            }
        });
    }

    @Override
    @Transactional
    public void updatePassword(final long id, final String newPassword) {
        LOGGER.debug("Password change for user with id: {}", id);
        userDao.updatePassword(id, passwordEncoder.encode(newPassword));
        LOGGER.info("Password changed successfully for user ID: {}", id);

    }

    @Override
    @Transactional
    public UserAuthInfo verifyEmailToken(final String token) {
        LOGGER.debug("Validating user with token: {}", token);
        handleTokenExpiration(token);
        Optional<Boolean> maybeValidated = userDao.findValidatedByTokenNotExpired(token);
        if(maybeValidated.isPresent() && maybeValidated.get() || maybeValidated.isEmpty()){
            LOGGER.warn("Token in use warn, with token: {}", token);
            throw new InvalidTokenException("Invalid token");
        }
        UserAuthInfo user = userDao.updateValidationAndFindAuthInfoByToken(token).orElseThrow(()-> new RuntimeException("Invalid token"));
        LOGGER.info("User validated, with token: {}", token);
        return user;
    }

    @Override
    @Transactional
    public void checkPasswordTokenValidity(final String token) {
        handleTokenExpiration(token);
        if (!isValidPasswordResetToken(token)) {
            LOGGER.warn("Invalid password reset token attempt: {}", token);
            throw new InvalidTokenException("Invalid password reset token");
        }
    }

    private void handleTokenExpiration(String token) {
        if(isTokenExpired(token)) {
            LOGGER.warn("Password reset token expired: {}", token);
            refreshToken(token);
            throw new ExpiredTokenException("Password reset token expired", token);
        }
    }


    @Override
    public Optional<User> findUserByEmail(final String email) {
        LOGGER.debug("Searching for user with email: {}", email);
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<UserAuthInfo> findAuthInfoByEmail(final String email) {
        LOGGER.debug("Searching for authUser with email: {}", email);
        return userDao.findAuthInfoByEmail(email);
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
    public Page<User> findUsers(final String search, final  PageParams pageParams) {
        LOGGER.debug("Getting all the users with search param: {} and pageParams: {}", search, pageParams);
        if (search == null || search.isEmpty()) {
            return userDao.findAll(pageParams);
        }
        return userDao.search(search, pageParams);
    }

    @Override
    @Transactional
    public void blockUser(final long userId) {
        LOGGER.debug("Attempting to block user with ID: {}", userId);
        User user = findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User does not exist for ID: {}", userId);
            return new IllegalStateException("User does not exist");
        });
        emailService.sendUserBlockedNotification(user);
        userDao.updateBlock(userId, true);
        LOGGER.info("User blocked successfully with ID: {}", userId);
    }

    @Override
    @Transactional
    public void unblockUser(final long userId) {
        LOGGER.debug("Attempting to unblock user with ID: {}", userId);
        User user = findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User does not exist for ID: {}", userId);
            return new IllegalStateException("User does not exist");
        });
        emailService.sendUserUnblockedNotification(user);
        userDao.updateBlock(userId, false);
        LOGGER.info("User unblocked successfully with ID: {}", userId);
    }

    @Override
    @Transactional
    public void refreshToken(final String oldToken) {
        LOGGER.debug("Attempting to refresh token, for oldToken: {}", oldToken);
        String uid = UUID.randomUUID().toString();
        User user = userDao.findByToken(oldToken).orElseThrow(() -> {
            LOGGER.error("User does not exist for token: {}", oldToken);
            return new IllegalStateException("User does not exist");
        });
        LocalDate date = LocalDate.now().plusDays(1);
        userDao.updateTokenAndExpirationByToken(uid, date,oldToken);
        emailService.sendValidationEmail(user,uid);
        LOGGER.info("Token refreshed successfully for oldToken: {}", oldToken);
    }

    @Override
    @Transactional
    public void refreshPasswordToken(final String oldToken) {
        LOGGER.debug("Attempting to refresh token, for oldToken: {}", oldToken);
        String uid = UUID.randomUUID().toString();

        User user = userDao.findByToken(oldToken).orElseThrow(() -> {
            LOGGER.error("User does not exist for token: {}", oldToken);
            return new IllegalStateException("User does not exist");
        });
        LocalDate date = LocalDate.now().plusDays(1);

        userDao.updateTokenAndExpirationByToken(uid, date,oldToken);
        emailService.sendForgotPassEmail(user, uid);
        LOGGER.info("Token refreshed successfully for oldToken: {}", oldToken);
    }


    private boolean isValidPasswordResetToken(String token) {
        LOGGER.debug("Checkin if password reset token is valid: {}", token);
        return userDao.existsByTokenNotExpired(token);
    }


    private boolean isTokenExpired(String token) {
        LOGGER.debug("Checkin if token has expired: {}", token);
        return userDao.existsByTokenExpired(token);
    }


    @Override
    public List<User> findEventAttendees(final long eventId) {
        LOGGER.debug("Getting attendees for event {}", eventId);
        return userDao.findAllAttendeesByEventId(eventId);
    }

    @Override
    public Page<User> findEventAttendees(final long eventId, PageParams pageParams) {
        LOGGER.debug("Getting attendees for event {} with pageParams {}", eventId, pageParams);
        return userDao.findAllAttendeesByEventId(eventId, pageParams);
    }


    @Override
    @Transactional
    public void resetPassword(final String token, final String newPassword) {
        checkPasswordTokenValidity(token);
        LOGGER.debug("updating new password for token: {}", token);
        userDao.updatePasswordAndClearTokenByToken(token, passwordEncoder.encode(newPassword));
        LOGGER.info("Password updated successfully for token: {}", token);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(final String email) {
        LOGGER.debug("Attempting to send forgot password email to: {}", email);
        User user = userDao.findByEmail(email).orElseThrow(()-> {
            LOGGER.error("User with email {} not found", email);
            return new RuntimeException("User does not exist");
        });
        if(!userDao.findValidationStatusByEmail(email)){
            LOGGER.warn("User with email {} not validated", email);
            throw new UserValidatedException("User not validated");
        }
        String uuid = UUID.randomUUID().toString();
        LocalDate date = LocalDate.now().plusDays(1);
        userDao.updateToken(user.getId(), uuid, date);
        emailService.sendForgotPassEmail(user, uuid);
        LOGGER.info("Forgot password email sent successfully to: {}", email);
    }

}
