package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.ExpiredPassTokenException;
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
    public User createUser(final String email,final  String username,final  String firstname,final  String lastname,final  String universityName, final String careerName,final  byte[] profilePicture, final List<String> interests,final  String password, final Locale locale) {
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
    public void changePassword(final long id,final  String newPassword) {
        LOGGER.debug("Changing password for user with ID: {}", id);
        userDao.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public Optional<UserAuthInfo> validateEmail(final String token) {
        if (userDao.existsByTokenExpired(token)) {
            throw new ExpiredTokenException("Token expired", token);
        }
        if(userDao.findValidatedByTokenNotExpired(token)){
            throw new InvalidTokenException("Token already used");
        }
        return userDao.updateValidationAndFindAuthInfoByToken(token);
    }




    @Override
    public Optional<User> findByEmail(final String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<UserAuthInfo> findByEmailWithPass(final String email) {
        return userDao.findAuthInfoByEmail(email);
    }

    @Override
    public Optional<User> findById(final long id) {
        return userDao.findById(id);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return userDao.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return userDao.existsByEmail(email);
    }


    @Override
    public Page<User> getAllUsers(final String search,final  PageParams pageParams) {

        if (search == null || search.isEmpty()) {
            return userDao.findAll(pageParams);
        }
        return userDao.search(search, pageParams);
    }

    @Override
    @Transactional
    public void blockUser(final long userId) {
        emailService.sendUserBlockedNotification(findById(userId).orElseThrow(()-> new IllegalStateException("User does not exist")));

        userDao.updateBlock(userId, true);
    }

    @Override
    @Transactional
    public void unblockUser(final long userId) {
        emailService.sendUserUnblockedNotification(findById(userId).orElseThrow(()-> new IllegalStateException("User does not exist")));
        userDao.updateBlock(userId, false);
    }
    @Override
    @Transactional
    public void refreshToken(final String oldToken) {
        String uid = UUID.randomUUID().toString();
        User user = userDao.findByToken(oldToken).orElseThrow(()-> new RuntimeException("User not found"));
        LocalDate date = LocalDate.now().plusDays(1);
        userDao.updateTokenAndExpirationByToken(uid, date,oldToken);
        emailService.sendValidationEmail(user,uid);
    }

    @Override
    @Transactional
    public void refreshPassToken(final String oldToken) {
        String uid = UUID.randomUUID().toString();

        User user = userDao.findByToken(oldToken).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDate date = LocalDate.now().plusDays(1);

        userDao.updateTokenAndExpirationByToken(uid, date,oldToken);
        emailService.sendForgotPassEmail(user, uid);

    }

    @Override
    @Transactional
    public void newPassword(final String token, final String newPassword) {
        if(userDao.existsByTokenExpired(token)){
            throw new ExpiredPassTokenException("Token expired", token);
        }
        if(!userDao.existsByTokenNotExpired(token)){
            throw new InvalidTokenException("Token already used");
        }
        userDao.updatePasswordByToken(token, passwordEncoder.encode(newPassword));

    }

    @Override
    @Transactional
    public void forgotPass(final String email) {
        User user = userDao.findByEmail(email).orElseThrow(()-> {
            LOGGER.warn("User with email {} not found", email);
            throw new RuntimeException("User does not exist");
        });


        if(!userDao.findValidationStatusByEmail(email)){
            throw new UserValidatedException("User not validated");
        }

        String uuid = UUID.randomUUID().toString();
        LocalDate date = LocalDate.now().plusDays(1);
        userDao.updateToken(user.getId(), uuid, date);
        emailService.sendForgotPassEmail(user, uuid);

    }

}
