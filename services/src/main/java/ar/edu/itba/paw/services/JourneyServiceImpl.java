package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class JourneyServiceImpl implements JourneyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyServiceImpl.class);
    private final JourneyResponseDao journeyResponseDao;

    private final JourneyDao journeyDao;
    private final UserService userService;
    private final EmailService emailService;
    private final UniversityService universityService;
    private final InterestService interestService;
    private final UserDao userDao;

    @Autowired
    public JourneyServiceImpl(final JourneyDao journeyDao, final UserService userService,final UserDao userDao,
                              final UniversityService universityService, final JourneyResponseDao journeyResponseDao, final EmailService emailService, final InterestService interestService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.emailService = emailService;
        this.interestService = interestService;
        this.userDao = userDao;
    }

    private void checkDates(final LocalDate startDate, final LocalDate endDate) {
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

    @Override
    @Transactional
    public Journey createJourney(final User user, final String destinationUniversity, final LocalDate startDate, final LocalDate endDate, final String description) {
        LOGGER.debug("Creating journey for {}", user);
        checkDates(startDate, endDate);

        University destination = universityService.findByName(destinationUniversity)
                .orElseThrow(() -> {
                    LOGGER.warn("Destination university not found: {}", destinationUniversity);
                    return new RuntimeException("Destination University not found");
                }
        );

        if (journeyDao.findOverlapping(user.getId(), startDate, endDate).isPresent()) {
            LOGGER.warn("User has an overlapping journey");
            throw new RuntimeException("There's already a journey registered in this time period");
        }

        return journeyDao.create(user, destination, startDate, endDate, description); // FIXME
    }

    @Override
    @Transactional
    public void replyToJourney(final String email, final long journeyId, final String message) {
        LOGGER.debug("Replying to journey {}", journeyId);

        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey with id {} not found", journeyId);
                    return new RuntimeException("Journey not found");}
                );

        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        journeyResponseDao.create(user.getId(), user.getUsername(), journeyId, message, LocalDateTime.now());

        List<Interest> interests = interestService.findByUserId(journey.getUser().getId());

        interestService.updateScoreByInterests(interests, user.getId());

        emailService.answerJourneyNotification(
                userDao.findAllJourneyResponders(journeyId),
                message,
                user,
                journey
        );

    }


    @Override
    public Page<Journey> getAllJourneys(final String search, final PageParams pageParams) {
        LOGGER.debug("Getting all journeys with search {}", search);
        if (search == null || search.isEmpty()) {
            return journeyDao.findAll(pageParams);
        }
        return journeyDao.search(search,pageParams);
    }

    @Override
    public Optional<Journey> getJourneyById(final long id) {
        return journeyDao.findById(id);
    }

    @Override
    public Optional<Journey> getJourneyByEmail(final String email) {
        // return journeyDao.findByUserEmail(email);
        long userId = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found")).getId();
        return journeyDao.findByUserId(userId);
        // ó deberíamos hacer lo siguiente?
        // return journeyDao.findByUserEmail(email); ¿? -> Acá no estaríamos verificando si existe el usuario
    }



    @Override
    public Page<Journey> getAllJourneys(final String search, final User user, final SortFieldJourney sortBy, final SortDirection direction,final  String destination,
                                        final LocalDate startDate, final LocalDate endDate, final String interest,
                                        final boolean isPast, final boolean isUpcoming,final  boolean isMyDestination, final boolean isOngoing,
                                        final PageParams pageParams) {
        LOGGER.debug("Getting filtered journeys");

        return journeyDao.search(search, user != null ? user.getId() : null, sortBy, direction, destination,
                startDate, endDate, interest, isPast, isUpcoming, isMyDestination, isOngoing,
                pageParams);

    }


    @Override
    public boolean userHasJourney(final String email) {
        Optional<User> maybeUser = userService.findByEmail(email);
        return maybeUser.filter(user -> journeyDao.findByUserId(user.getId()).isPresent()).isPresent();
    }
    @Override
    public boolean userHasJourney(final User user) {
        return journeyDao.findByUserId(user.getId()).isPresent();
    }


    //@Todo tendria mas sentido q reciba pageParams y que el controller le mande 1, limit.
    @Override
    public List<Journey> getRecommendedJourneys(final String email, final int limit) {
        if(limit <= 0 ){
            throw new IllegalArgumentException("Limit must be grater than 0");
        }
        if(userHasJourney(email)){
            return journeyDao.findRecommended(email, new PageParams(1, limit)).getContent();
        }
        Optional<User> maybeUser = userService.findByEmail(email);
        if(maybeUser.isEmpty()){
            return journeyDao.findAll(new PageParams(1, limit)).getContent();
        }
        List<Journey> journeys = journeyDao.findByOriginCity(maybeUser.get().getUniversity().getCity().getId(), new PageParams(1, limit)).getContent();
        if(journeys.isEmpty()){
            return journeyDao.findAll( new PageParams(1, limit)).getContent();
        }
        return journeys ;
    }



    @Override
    @Transactional
    public void delete(final long id, final String message) {
        journeyDao.updateDeletionMessage(id, message);
        journeyResponseDao.deleteByJourneyId(id);
        Journey journey = journeyDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journey not found"));
        emailService.sendJourneyDeletionNotification(journey,message);
        journeyDao.delete(id);
    }

    @Override
    public boolean isJourneyOwnedByUser(final String email,final  long journeyID) {
        Optional<Journey> journey = journeyDao.findById(journeyID);
        return journey.isPresent() && journey.get().getUser().getEmail().equals(email);
    }

    @Override
    @Transactional
    public void editJourney(final long journeyId,final  String destinationUniversity, final LocalDate startDate, final LocalDate endDate, final String description) {
        University university = universityService.findByName(destinationUniversity)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found: {}", destinationUniversity);
                    return new IllegalArgumentException("University not found");
                });
        journeyDao.update(journeyId, university, startDate, endDate, description);
    }


    @Override
    public Optional<JourneyResponse> findJourneyResponseById(final long id) {
        return journeyResponseDao.findById(id);
    }

    @Override
    @Transactional
    public void deleteJourneyResponse(final long id, final String message) {

        JourneyResponse deletedComment = findJourneyResponseById(id).orElseThrow(() -> new IllegalArgumentException("Journey response doesn't exists"));
        Journey journey = journeyDao.findById(deletedComment.getJourneyId()).orElseThrow(()->new IllegalStateException("Journey from journey response doesn't exist"));
        User commentAuthor = userService.findById(deletedComment.getUserId()).orElseThrow(() -> new IllegalArgumentException("User from journey response doesn't exists"));

        emailService.sendJourneyCommentDeletionNotification(deletedComment,journey,commentAuthor,message);

        journeyResponseDao.updateDeletionMessage(id, message);
        journeyResponseDao.delete(id);
    }

    @Override
    public long getJourneyIdByResponseId(final long journeyResponseId) {
        return journeyResponseDao.findJourneyIdByResponseId(journeyResponseId);
    }



    @Override
    public Page<JourneyResponse> listAllResponsesFromJourney(final long journeyId, final PageParams pageParams) {
        return journeyResponseDao.listAllByJourneyId(journeyId, pageParams);
    }

    @Override
    public int getJourneyResponseCount(final long id) {
        return journeyResponseDao.countByJourneyId(id);
    }
}

