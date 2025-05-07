package ar.edu.itba.paw.services;

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


    // TODO: ¿Está bien esto? -> ¿O debería resolverse en el DAO?
    @Override
    @Transactional
    public User createUser(String email, String username, String firstname, String lastname, String universityName, String careerName, byte[] profilePicture, long[] interests, String password, Locale locale) {
        LOGGER.debug("Creating user for {}", email);

        University university = universityService.findByName(universityName).orElseThrow(() -> new RuntimeException("University not found")); // TODO: ¿Acá cuando tira excepción debería haber un log?
        Career career = careerService.findByName(careerName).orElseThrow(() -> new RuntimeException("Career not found"));

        long profilePictureId = imageService.storeImage(profilePicture);

        User user = userDao.create(email, username, firstname, lastname, university, career, profilePictureId, passwordEncoder.encode(password), locale);

        interestService.saveUserInterests(interests, user.getId());
        
        return user;
    }



    @Override
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
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
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
    public void updateProfilePicture(long userId, byte[] profilePicture) {
        LOGGER.debug("Updating profile picture for user {}", userId);
        long profilePictureId = imageService.storeImage(profilePicture);
        userDao.updateProfilePicture(userId, profilePictureId);
    }

    @Override
    @Transactional
    public void updateProfileInfo(long userId, String firstname, String lastname, String username) {
        LOGGER.debug("Updating profile info for user {}: firstname={}, lastname={}, username={}", userId, firstname, lastname, username);

//        // FIXME: todas estas validaciones creo que no hay que ponerlas
//        Optional<User> existingUser = findById(userId);
//        if (existingUser.isPresent() && !existingUser.get().getUsername().equals(username)) {
//            if (existsByUsername(username)) {
//                LOGGER.warn("Username {} is already taken", username);
//                throw new IllegalArgumentException("Username is already taken");
//            }
//        }

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
//
//        University university = universityService.findByName(newUniversityName)
//                .orElseThrow(() -> {
//                    LOGGER.warn("University not found: {}", newUniversityName);
//                    return new IllegalArgumentException("University not found");
//                });
//
//        userDao.updateUniversity(userId, university.getId());
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
//
//        // FIXME: ¿debería ser así o directamente en el dao crear un método updateCareer(long userId, String careerName)?
//        Career career = careerService.findByName(newCareerName)
//                .orElseThrow(() -> {
//                    LOGGER.warn("Career not found: {}", newCareerName);
//                    return new IllegalArgumentException("Career not found");
//                });
//
//        userDao.updateCareer(userId, career.getId());
        userDao.updateCareer(userId, newCareerName);
    }

    @Override
    @Transactional
    public void updateCareer(long userId, long careerId) {
        LOGGER.debug("Updating career for user {} to career ID {}", userId, careerId);
//
//        // FIXME: está validación no la deberíamos hacer, ya está validada en webapp, o no?
//        // Si llegara a no existir sería una condición anormal y persistencia nos tiraría una excepción al querer realizar el update
//        careerService.findById(careerId)
//                .orElseThrow(() -> {
//                    LOGGER.warn("Career not found with ID: {}", careerId);
//                    return new IllegalArgumentException("Career not found");
//                });

        userDao.updateCareer(userId, careerId);
        LOGGER.info("Successfully updated career for user {} to career ID {}", userId, careerId);
    }



    // Recibe User -> ¿Está bien?
    //@TODO ask (exception?). @TODO add cache?
    @Override
    @Transactional(readOnly = true)
    public byte[] getProfilePictureData(User user) {
        return imageService.getImage(user.getProfilePictureId())
                .orElseThrow(() -> new IllegalStateException("User does not have a profile picture"))
                .getData();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(String search, PageParams pageParams) {

        if (search == null || search.isEmpty()) {
            return userDao.getAllUsers(pageParams.getPage(), pageParams.getSize());
        }
        return userDao.searchUsers(search, pageParams.getPage(), pageParams.getSize());
    }

    @Override
    @Transactional
    public void blockUser(long userId) {
        userDao.blockUser(userId);
    }

    @Override
    @Transactional
    public void unblockUser(long userId) {
        userDao.unblockUser(userId);
    }

}
