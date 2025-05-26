package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.InvalidException;
import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import ar.edu.itba.paw.models.exceptions.JourneyResponseNotFoundException;
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


    @Autowired
    public JourneyServiceImpl(final JourneyDao journeyDao, final UserService userService,
                              final UniversityService universityService, final JourneyResponseDao journeyResponseDao, final EmailService emailService, final InterestService interestService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.emailService = emailService;
        this.interestService = interestService;
    }

    private void checkDates(final LocalDate startDate, final LocalDate endDate) {
        if(startDate == null || endDate == null) {
            LOGGER.warn("Start date or end date is null");
            throw new RuntimeException("Start date and end date cannot be null");
        }
        if(startDate.isAfter(endDate)) {
            LOGGER.warn("Start date is after end date");
            throw new RuntimeException("Start date cannot be after end date");
        }
        if(startDate.isBefore(LocalDate.now())) {
            LOGGER.warn("Start date is before today");
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

        Journey journey = journeyDao.create(user, destination, startDate, endDate, description);
        LOGGER.info("Journey created: {}", journey);
        return journey;
    }

    @Override
    @Transactional
    public void createJourneyResponse(final String email, final long journeyId, final String message) {
        LOGGER.debug("Replying to journey {}", journeyId);
        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey with id {} not found", journeyId);
                    return new RuntimeException("Journey not found");
                });

        User responder = userService.findUserByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found", email);
                    return new RuntimeException("User not found");
                });

        journeyResponseDao.create(responder, journey, message);

        interestService.updateMatchingInterestScores(responder.getId(), journey.getUser().getId());
        LOGGER.info("Interest scores updated for responder {}", responder.getId());

        int page = 1;
        int pageSize = 50;
        Page<User> respondersPage;

        do {
            respondersPage = journeyResponseDao.findRespondersByJourneyId(
                    journeyId,
                    new PageParams(page, pageSize)
            );

            List<User> responders = respondersPage.getContent();

            if (!responders.isEmpty()) {
                emailService.answerJourneyNotification(
                        responders,
                        message,
                        responder,
                        journey
                );
            }

            page++;
        } while (page <= respondersPage.getTotalPages());
        LOGGER.info("Journey response notifications sent to all responders for journey {}", journeyId);

        emailService.answerJourneyOwnerNotification(message, responder, journey);
        LOGGER.info("Journey response notifications sent to owner for journey {}", journeyId);
    }


    @Override
    public Page<Journey> findJourneys(final String search, final PageParams pageParams) {
        LOGGER.debug("Getting all journeys with search {}", search);
        if (search == null || search.isEmpty()) {
            return journeyDao.findAll(pageParams);
        }
        return journeyDao.search(search,pageParams);
    }

    @Override
    public Optional<Journey> getJourneyById(final long id) {
        LOGGER.debug("Getting journey by id {}", id);
        return journeyDao.findById(id);
    }

    @Override
    public Optional<Journey> getJourneyByEmail(final String email) {
        LOGGER.debug("Getting journey by email {}", email);
        User user = userService.findUserByEmail(email).orElseThrow(() -> {
            LOGGER.warn("User with email {} not found", email);
            return new RuntimeException("User not found");
        });
        return Optional.ofNullable(user.getJourney());
    }



    @Override
    public Page<Journey> findJourneys(final String search, final User user, final SortFieldJourney sortBy, final SortDirection direction, final  String destination,
                                      final LocalDate startDate, final LocalDate endDate, final String interest,
                                      final boolean isPast, final boolean isUpcoming, final  boolean isMyDestination, final boolean isOngoing,
                                      final PageParams pageParams) {
        LOGGER.debug("Getting filtered journeys");
        if(user != null && isMyDestination && ! existsByUser(user)){
            LOGGER.warn("User has no journeys");
            throw new InvalidException("User has no journeys");
        }
        return journeyDao.search(search, user != null ? user.getId() : null, sortBy, direction, destination,
                startDate, endDate, interest, isPast, isUpcoming, isMyDestination, isOngoing,
                pageParams);

    }


    @Override
    public boolean existsByUserEmail(final String email) {
        LOGGER.debug("Checking if user has journey {}", email);
        User user = userService.findUserByEmail(email).orElseThrow(() -> {
            LOGGER.warn("User with email '{}' not found", email);
            return new RuntimeException("User not found");
        });
        return user.getJourney() != null; //@todo check
    }

    @Override
    public boolean existsByUser(final User user) {
        LOGGER.debug("Checking if user has journey {}", user);
        return user.getJourney() != null;
    }


    @Override
    public List<Journey> findRecommendedJourneys(final String email, final int limit) {
        LOGGER.debug("Getting recommended journeys for {}", email);
        if(limit <= 0 ){
            LOGGER.warn("Limit must be greater than 0");
            throw new IllegalArgumentException("Limit must be grater than 0");
        }
        if(existsByUserEmail(email)){
            return journeyDao.findRecommended(email, new PageParams(1, limit)).getContent();
        }
        Optional<User> maybeUser = userService.findUserByEmail(email);
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
    public void deleteJourney(final long id, final String message) {

        Journey journey = journeyDao.findById(id).orElseThrow(() -> {
                LOGGER.warn("Journey with id {} not found", id);
                return new JourneyNotFoundException("Journey not found");
        }
        );

        LOGGER.info("Journey deletion message updated: {}", message);
        journey.setDeletionMessage(message);

        LOGGER.debug("Deleting journey {}", id);
        journey.setDeletionMessage(message);

        LOGGER.info("Journey deletion message updated: {}", message);
        journeyResponseDao.deleteByJourneyId(journey.getId()); // todo check

        LOGGER.info("Journey responses deleted for journey {}", id);

        emailService.sendJourneyDeletionNotification(journey,message);
        LOGGER.info("Journey deletion notification sent to user {}", journey.getUser().getEmail());

        journey.setDeleted(true);
        LOGGER.info("Journey deleted: {}", id);
    }

    @Override
    public boolean isJourneyOwnedByUser(final String email, final long journeyID) {
        LOGGER.debug("Checking if journey {} is owned by user {}", journeyID, email);
        Optional<Journey> journey = journeyDao.findById(journeyID);
        return journey.isPresent() && journey.get().getUser().getEmail().equals(email);
    }

    @Override
    public boolean isJourneyOwnedByUser(Journey journey, User user) {
        if (journey == null || user == null) {
            return false;
        }

        User journeyUser = journey.getUser();
        if (journeyUser == null || journeyUser.getId() == null || user.getId() == null) {
            return false;
        }

        return journeyUser.getId().equals(user.getId());
    }

    @Override
    @Transactional
    public void updateJourney(final long journeyId, final  String destinationUniversity, final LocalDate startDate, final LocalDate endDate, final String description) {
        LOGGER.debug("Editing journey {}", journeyId);
        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey with id {} not found", journeyId);
                    return new JourneyNotFoundException("Journey not found");
                });
        University university = universityService.findByName(destinationUniversity)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found: {}", destinationUniversity);
                    return new IllegalArgumentException("University not found");
                });
        journey.setDestinationUniversity(university);
        journey.setStartDate(startDate);
        journey.setEndDate(endDate);
        journey.setDescription(description);
        LOGGER.info("Journey updated: {}", journeyId);
    }


    @Override
    public Optional<JourneyResponse> findJourneyResponseById(final long id) { // fixme:mover esto al journeyDao
        LOGGER.debug("Finding journey response by id {}", id);

        return journeyResponseDao.findById(id);
    }

    @Override
    @Transactional
    public void deleteJourneyResponse(final long id, final String message) {
        LOGGER.debug("Deleting journey response {}", id);
        JourneyResponse journeyResponse = findJourneyResponseById(id).orElseThrow(() -> {
            LOGGER.error("Journey response with id {} not found", id);
            return new JourneyResponseNotFoundException("Journey response doesn't exists");}
        );

        User commentAuthor = journeyResponse.getUser();

        emailService.sendJourneyCommentDeletionNotification(journeyResponse, journeyResponse.getJourney(), commentAuthor, message);
        LOGGER.info("Journey response deletion notification sent to user {}", commentAuthor.getEmail());

        journeyResponse.setDeletionMessage(message);
        LOGGER.info("Journey response deletion message updated: {}", message);

        journeyResponse.setDeleted(true);
        LOGGER.info("Journey response deleted: {}", id);
    }


    @Override
    public Page<JourneyResponse> findJourneyResponses(final long journeyId, final PageParams pageParams) {
        LOGGER.debug("Finding all journey responses for journey {}", journeyId);
        return journeyResponseDao.findAllByJourneyId(journeyId, pageParams);
    }

    @Override
    public int countJourneyResponses(final long id) {
        return journeyResponseDao.countByJourneyId(id);
    }
}





//
//    @Override
//    public long findJourneyIdByResponseId(final long journeyResponseId) {
//        JourneyResponse journeyResponse = findJourneyResponseById(journeyResponseId).orElseThrow(() -> {
//            LOGGER.error("Journey response with id {} not found", journeyResponseId);
//            return new JourneyResponseNotFoundException("Journey response doesn't exists");}
//        );
//        return journeyResponse.getJourney().getId();
//    }