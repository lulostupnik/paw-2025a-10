package ar.edu.itba.paw.services;

//import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
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
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class JourneyServiceImpl implements JourneyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyServiceImpl.class);

    private final JourneyDao journeyDao;
    private final UserService userService;
    private final EmailService emailService;
    private final UniversityService universityService;
    private final JourneyResponseService journeyResponseService;

    private final InterestService interestService;
    private final UserDao userDao;

    @Autowired
    public JourneyServiceImpl(JourneyDao journeyDao, UserService userService,UserDao userDao,
                              UniversityService universityService, JourneyResponseService journeyResponseService, EmailService emailService, InterestService interestService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseService = journeyResponseService;
        this.emailService = emailService;
        this.interestService = interestService;
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
    }

    // FIXME: ¿CachePut?
    @Transactional
    @Override
    public Journey createJourney(User user, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
        LOGGER.debug("Creating journey for {}", user);
        checkDates(startDate, endDate);

        University destination = universityService.findByName(destinationUniversity)
                .orElseThrow(() -> {
                    LOGGER.warn("Destination university not found: {}", destinationUniversity);
                    return new RuntimeException("Destination University not found");
                }
        );

        if (journeyDao.findOverlappingJourney(user.getId(), startDate, endDate).isPresent()) {
            LOGGER.warn("User has an overlapping journey");
            throw new RuntimeException("There's already a journey registered in this time period");
        }

        return journeyDao.create(user, destination, startDate, endDate, description); // FIXME
    }

    @Transactional
    @Override
    public void replyToJourney(String email, long journeyId, String message) {
        LOGGER.debug("Replying to journey {}", journeyId);

        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey with id {} not found", journeyId);
                    return new RuntimeException("Journey not found");}
                );

        LOGGER.debug("Looking for user {}", email);
        //parche temporal buscar por username
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        journeyResponseService.create(user.getId(), user.getUsername() ,journeyId, message, LocalDateTime.now());

        List<Interest> interests = interestService.findByUserId(journey.getUser().getId());
        interestService.updateScoreByInterests(interests, user.getId());

        emailService.answerJourneyNotification(
                userDao.listJourneyRespondersMinusUsers(journeyId),
                message,
                user,
                journey
        );


    }

    @Override
    public List<Journey> getAllJourneys() {
        return journeyDao.listAll();
    }

    @Override
    public Page<Journey> getAllJourneys(String search, PageParams pageParams) {
        LOGGER.debug("Getting all journeys with search {}", search);
        if (search == null || search.isEmpty()) {
            return journeyDao.listAll(pageParams.getPage(), pageParams.getSize());
        }
        return journeyDao.searchJourneys(search,pageParams.getPage(), pageParams.getSize());
    }

    @Override
    public Optional<Journey> getJourneyById(long id) {
        return journeyDao.findById(id);
    }

    @Override
    public Optional<Journey> getJourneyByEmail(String email) {
        long userId = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found")).getId();
        return journeyDao.findByUserId(userId);
        // ó deberíamos hacer lo siguiente?
        // return journeyDao.findByUserEmail(email); ¿? -> Acá no estaríamos verificando si existe el usuario
    }



    @Override
    public Page<Journey> getAllJourneys(String search, User user, String sortBy, String direction, Long destination, LocalDate startDate, LocalDate endDate, Long interest, PageParams pageParams) {
        LOGGER.debug("Getting filtered journeys");
        if(direction == null || direction.isEmpty()){
            direction = "asc";
        } else if(! direction.equals("asc") && ! direction.equals("desc")){
            throw new IllegalArgumentException("Invalid direction parameter");
        }

        if(sortBy == null || sortBy.isEmpty()){
            sortBy = "start_date";
        } else if (! sortBy.equals("start_date") && ! sortBy.equals("end_date") && ! sortBy.equals("city") && ! sortBy.equals("interest")) {
            throw new IllegalArgumentException("Invalid sortBy parameter");
        }

        return journeyDao.searchJourneys(search, user != null ? user.getId() : null, sortBy, direction, destination, startDate, endDate, interest, pageParams.getPage(), pageParams.getSize());

    }


    @Override
    public Boolean userHasJourney(String email) {
        Optional<User> maybeUser = userService.findByEmail(email);
        return maybeUser.filter(user -> journeyDao.findByUserId(user.getId()).isPresent()).isPresent();
    }
    @Override
    public boolean userHasJourney(User user) {
        return journeyDao.findByUserId(user.getId()).isPresent();
    }

    // FIXME: Configurar la cache para que guarde los resultados por un tiempo (30min) y después meter acá el @Cacheable
    // @Cacheable(value = "journeysRecommended", key = "#email")
    @Override
    public List<Journey> getRecommendedJourneys(String email, int limit) {
        if(limit <= 0 ){
            throw new IllegalArgumentException("Limit must be grater than 0");
        }
        if(userHasJourney(email)){
            return journeyDao.getRecommendedJourneys(email, 1, limit).getContent();
        }
        Optional<User> maybeUser = userService.findByEmail(email);
        if(maybeUser.isEmpty()){
            return journeyDao.listAll(1, limit).getContent();
        }
        List<Journey> journeys = journeyDao.findByOriginCity(maybeUser.get().getUniversity().getCity().getId(), 1, limit).getContent();
        if(journeys.isEmpty()){
            return journeyDao.listAll( 1, limit).getContent();
        }
        return journeys ;
    }

    @Override
    public List<Journey> getJourneysByUser(String email) {
        return journeyDao.getJourneysByUser(email);
    }

    // Yo creería que mejor no cachear, pero no estoy seguro
    @Override
    public List<JourneyResponse> getJourneyResponses(long journeyId){
        return journeyResponseService.listAllFromJourney(journeyId);
    }

    @Override
    public List<Journey> getOthersJourneys(long userId) {
        return journeyDao.getOthersJourneys(userId);
    }

    @Override
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

    @Transactional
    @Override
    public void delete(long id, String message) {
        journeyDao.deletionMessage(id, message);
        journeyResponseService.deleteByJourneyId(id);
        Journey journey = journeyDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journey not found"));
        emailService.sendJourneyDeletionNotification(journey,message);
        journeyDao.delete(id);
    }

    @Override
    public boolean isJourneyOwnedByUser(String email, long journeyID) {
        Optional<Journey> journey = journeyDao.findById(journeyID);
        return journey.isPresent() && journey.get().getUser().getEmail().equals(email);
    }

    @Transactional
    @Override
    public void editJourney(long journeyId, String destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
        University university = universityService.findByName(destinationUniversity)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found: {}", destinationUniversity);
                    return new IllegalArgumentException("University not found");
                });
        journeyDao.updateData(journeyId, university, startDate, endDate, description);
    }
}
