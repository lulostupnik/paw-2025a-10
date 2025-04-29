package ar.edu.itba.paw.services;

//import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
//import java.util.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JourneyServiceImpl implements JourneyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyServiceImpl.class);

    private final JourneyDao journeyDao;
    private final UserService userService;
    private final EmailService emailService;
    private final UniversityService universityService;
    private final JourneyResponseDao journeyResponseDao;
    //private final CityService cityService;
    //private final InterestService interestService;
    private final ImageService imageService;
    private final InterestService interestService;
    private final UserDao userDao;

    @Autowired
    public JourneyServiceImpl(JourneyDao journeyDao, UserService userService, UserDao userDao, ImageService imageService,
                              UniversityService universityService, JourneyResponseDao journeyResponseDao, EmailService emailService, CityService cityService, InterestService interestService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.emailService = emailService;
        //this.cityService = cityService;
        this.interestService = interestService;
        this.imageService = imageService;
        this.userDao = userDao;
    }

    private void checkDates(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            throw new RuntimeException("Start date and end date cannot be null");
        }
        if(startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }
        if(startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be before today");
        }
        if(endDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("End date cannot be before today");
        }
    }

    // FIXME: ¿CachePut?
    @Transactional
    @Override
    public Journey createJourney(String email, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
        LOGGER.debug("Creating journey for {}", email);
        checkDates(startDate, endDate);

        LOGGER.debug("Looking for university {}", destinationUniversity);
        University destination = universityService.findByAny(destinationUniversity).orElseThrow(() -> new RuntimeException("Destination University not found"));

        LOGGER.debug("Looking for user {}", email);
        //Solo por ahora busco tmbn x username
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        if (journeyDao.findOverlappingJourney(user.getId(), startDate, endDate).isPresent()) {
            LOGGER.info("User has an overlapping journey");
            throw new RuntimeException("There's already a journey registered in this time period");
        }
        if(journeyDao.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("User already has a journey");
        }

        LOGGER.info("Journey data is valid, commiting new event to persistance");
        return journeyDao.create(user, destination, startDate, endDate, description); // FIXME
    }

    @Transactional
    @Override
    public void replyToJourney(String email, long journeyId, String message) {
        LOGGER.debug("Replying to journey {}", journeyId);

        LOGGER.debug("Looking for journey {}", journeyId);
        Journey journey = journeyDao.findById(journeyId).orElseThrow(() -> new RuntimeException("Journey not found"));

        LOGGER.debug("Looking for user {}", email);
        //parche temporal buscar por username
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        LOGGER.info("Journey reply is valid, commiting new reply to persistance");
        journeyResponseDao.create(user.getId(), user.getUsername() ,journeyId, message, LocalDateTime.now());


        LOGGER.info("Updating interest score");
        List<Interest> interests = interestService.findByUserId(journey.getUser().getId());
        interestService.updateScoreByInterests(interests, user.getId());


//
//        emailService.answerJourneyMail(
//                journey.getUser(),
//                message,
//                user,
//                journey
//        );

//        LOGGER.info("Notifying all commenters in journey about a new comment");

        emailService.answerJourneyNotification(
                userDao.listJourneyRespondersMinusUsers(journeyId/*, new ArrayList<>(List.of(user.getId(), journey.getUser().getId()))*/),
                message,
                user,
                journey
        );


    }

    @Transactional(readOnly = true)
    @Override
    public List<Journey> getAllJourneys() {
        return journeyDao.listAll();
    }

    @Transactional(readOnly = true)
    // @Cacheable(value = "journeysById", key = "#id") -> por ahora no cacheo porque cuando cambio un user tengo que invalidar esta cache y para eso tengo que encontrar este journey asociado a ese user
    @Override
    public Optional<Journey> getJourneyById(long id) {
        return journeyDao.findById(id);
    }

    // Por ahora no es cacheable porque no se como hacer el CacheEvict cuando en el update no se retorna nada.
    @Transactional(readOnly = true)
    @Override
    public Optional<Journey> getJourneyByEmail(String email) {
        long userId = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found")).getId();
        return journeyDao.findByUserId(userId);
        // ó deberíamos hacer lo siguiente?
        // return journeyDao.findByUserEmail(email); ¿? -> Acá no estaríamos verificando si existe el usuario
    }

    @Override
    @Transactional(readOnly = true)
    public List<Journey> getFilteredJourneys(String destination, LocalDate startDate, LocalDate endDate, String interest) {
        return journeyDao.findByFilters(destination, startDate,endDate, interest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Journey> getFilteredJourneys(String email, String destination, LocalDate startDate, LocalDate endDate, String interest) {
        LOGGER.debug("Getting filtered journeys excluding user {}", email);

        User user = userService.findByEmail(email).orElseThrow(() -> {
            LOGGER.warn("User not found with email: {}", email);
            return new RuntimeException("User not found");
        });

        return journeyDao.findByFilters(user.getId(), destination, startDate, endDate, interest);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean userHasJourney(String email) {
        Optional<User> maybeUser = userService.findByEmail(email);
        if(maybeUser.isEmpty()){
            return false;
        }
        return journeyDao.findByUserId(maybeUser.get().getId()).isPresent();
    }

    // FIXME: Configurar la cache para que guarde los resultados por un tiempo (30min) y después meter acá el @Cacheable
    @Transactional(readOnly = true)
    // @Cacheable(value = "journeysRecommended", key = "#email")
    @Override
    public List<Journey> getRecommendedJourneys(String email) {
        if(userHasJourney(email)){
            return journeyDao.getRecommendedJourneys(email);
        }
        Optional<User> maybeUser = userService.findByEmail(email);
        if(maybeUser.isEmpty()){
            return journeyDao.listAll();
        }
        List<Journey> journeys = journeyDao.findByOriginCity(maybeUser.get().getUniversity().getCity().getId());
        if(journeys.isEmpty()){
            return journeyDao.listAll();
        }
        return journeys ;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Journey> getJourneysByUser(String email) {
        return journeyDao.getJourneysByUser(email);
    }

    // Yo creería que mejor no cachear, pero no estoy seguro
    @Transactional(readOnly = true)
    @Override
    public List<JourneyResponse> getJourneyResponses(long journeyId){
        return journeyResponseDao.listAllFromJourney(journeyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Journey> getOthersJourneys(long userId) {
        return journeyDao.getOthersJourneys(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Journey> getOthersJourneys(String email) {
        long userId = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")).getId();
        return getOthersJourneys(userId);
    }

    @Override
    @Transactional
    // @CacheEvict(value = "journeysById", key = "#journeyId")
    public void updateJourneyDates(long journeyId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("Updating dates for journey {}: start={}, end={}", journeyId, startDate, endDate);

        checkDates(startDate, endDate);

        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey not found with ID: {}", journeyId);
                    return new IllegalArgumentException("Journey not found");
                });

        Optional<Journey> overlapping = journeyDao.findOverlappingJourney(journey.getUser().getId(), startDate, endDate);
        if (overlapping.isPresent() && overlapping.get().getId() != journeyId) {
            LOGGER.warn("User has an overlapping journey");
            throw new RuntimeException("There's already a journey registered in this time period");
        }

        journeyDao.updateDates(journeyId, startDate, endDate);
        LOGGER.info("Successfully updated dates for journey {}", journeyId);
    }

    @Override
    @Transactional
    // @CacheEvict(value = "journeysById", key = "#journeyId")
    public void updateJourneyDescription(long journeyId, String description) {
        LOGGER.debug("Updating description for journey {}", journeyId);

        // Verify journey exists
        journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey not found with ID: {}", journeyId);
                    return new IllegalArgumentException("Journey not found");
                });

        journeyDao.updateDescription(journeyId, description);
        LOGGER.info("Successfully updated description for journey {}", journeyId);
    }

    @Override
    @Transactional
    // @CacheEvict(value = "journeysById", key = "#journeyId")
    public void updateJourneyDestination(long journeyId, String universityName) {
        LOGGER.debug("Updating destination for journey {} to {}", journeyId, universityName);

        University university = universityService.findByName(universityName)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found: {}", universityName);
                    return new IllegalArgumentException("University not found");
                });

        journeyDao.updateDestinationUniversity(journeyId, university.getId());
        LOGGER.info("Successfully updated destination for journey {} to {}", journeyId, universityName);
    }

    @Override
    @Transactional
    // @CacheEvict(value = "journeysById", key = "#journeyId")
    public void updateJourneyDestination(long journeyId, long universityId) {
        LOGGER.debug("Updating destination for journey {} to university ID {}", journeyId, universityId);

        // Validate that university exists
        universityService.findById(universityId)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found with ID: {}", universityId);
                    return new IllegalArgumentException("University not found");
                });

        journeyDao.updateDestinationUniversity(journeyId, universityId);
        LOGGER.info("Successfully updated destination for journey {} to university ID {}", journeyId, universityId);
    }

}
