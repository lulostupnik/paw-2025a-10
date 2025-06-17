package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.persistence.TipDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class JourneyServiceImpl implements JourneyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JourneyServiceImpl.class);
    private final JourneyResponseDao journeyResponseDao;
    private final TipDao tipDao;
    private final JourneyDao journeyDao;
    private final UserService userService;
    private final EmailService emailService;
    private final UniversityService universityService;
    private final InterestService interestService;


    @Autowired
    public JourneyServiceImpl(final JourneyDao journeyDao, final UserService userService,
                              final UniversityService universityService, final JourneyResponseDao journeyResponseDao, TipDao tipDao, final EmailService emailService, final InterestService interestService) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.tipDao = tipDao;
        this.emailService = emailService;
        this.interestService = interestService;
    }

    private void checkDates(final LocalDate startDate, final LocalDate endDate) {
        if(startDate == null || endDate == null) {
            throw new InvalidDateException("Start date and end date cannot be null");
        }
        if(startDate.isAfter(endDate)) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        if(startDate.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Start date cannot be before today");
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
                    return new UniversityNotFoundException(destinationUniversity);
                }
        );

        Journey existingJourney = user.getJourney();
        if (existingJourney != null) {
            if (!existingJourney.isDeleted()) {
                LOGGER.warn("User {} already has an active journey", user.getId());
                throw new UserWithActiveJourneyException(user.getId());
            }
            // Hard delete the soft-deleted journey and its responses
            LOGGER.info("Hard deleting previous journey {} and its responses for user {}", existingJourney.getId(), user.getId());
            tipDao.deleteByJourney(existingJourney.getId());
            journeyResponseDao.hardDeleteByJourneyId(existingJourney.getId());
            journeyDao.hardDelete(existingJourney);

            user.setJourney(null);
        }

        Journey journey = journeyDao.create(user, destination, startDate, endDate, description);
        LOGGER.info("Journey created: {}", journey);
        return journey;
    }

    @Override
    @Transactional
    public JourneyResponse createJourneyResponse(final String email, final long journeyId, final String message) {
        LOGGER.debug("Replying to journey {}", journeyId);
        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey with id {} not found", journeyId);
                    return new JourneyNotFoundException(journeyId);
                });

        User responder = userService.findUserByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found", email);
                    return new UserNotFoundException(email);
                });

        JourneyResponse journeyResponse = journeyResponseDao.create(responder, journey, message);

        interestService.updateMatchingInterestScores(responder.getId(), journey.getUser().getId());
        LOGGER.info("Interest scores updated for responder {}", responder.getId());

        int page = 1;
        int pageSize = 50;
        Page<User> respondersPage;

        EmailJourney emailJourney = new EmailJourney(journey);
        EmailUser emailResponder = new EmailUser(responder);
        do {
            respondersPage = journeyResponseDao.findRespondersByJourneyId(
                    journeyId,
                    new PageParams(page, pageSize)
            );


            List<EmailUser> responders = respondersPage.getContent().stream()
                    .map(EmailUser::new)
                    .toList();

            if (!responders.isEmpty()) {
                emailService.answerJourneyNotification(
                        responders,
                        message,
                        emailResponder,
                        emailJourney
                );
            }

            page++;
        } while (page <= respondersPage.getTotalPages());
        LOGGER.info("Journey response notifications sent to all responders for journey {}", journeyId);

        emailService.answerJourneyOwnerNotification(message, emailResponder, emailJourney);
        LOGGER.info("Journey response notifications sent to owner for journey {}", journeyId);
        return journeyResponse;
    }

    private Page<Journey> searchByTerm(final String searchTerm, final PageParams pageParams){
        return journeyDao.search(
                searchTerm,
                null,
                null,
                null,
                null,
                null,
                null,
                null,

                false,

                pageParams
        );
    }

    @Override
    public Page<Journey> findJourneys(final String search, final PageParams pageParams) {
        LOGGER.debug("Getting all journeys with search {}", search);
        if (search == null || search.isEmpty()) {
            return journeyDao.findAll(pageParams);
        }
        return searchByTerm(search, pageParams);
    }

    @Override
    public Optional<Journey> findJourneyById(final long id) {
        LOGGER.debug("Getting journey by id {}", id);
        return journeyDao.findById(id);
    }

    private LocalDate capEndDateForPastJourneys(LocalDate endDate) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return endDate == null || endDate.isAfter(yesterday) ? yesterday : endDate;
    }

    private LocalDate ensureStartDateForUpcomingJourneys(LocalDate startDate) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return startDate == null || startDate.isBefore(tomorrow) ? tomorrow : startDate;
    }


    @Override
    public Page<Journey> findJourneys(final String search, final User user, final SortFieldJourney sortBy, final SortDirection direction, final  String destination,
                                      final LocalDate startDate, final LocalDate endDate, final String interest,
                                      final boolean isPast, final boolean isUpcoming, final  boolean isMyDestination, final boolean isOngoing,
                                      final PageParams pageParams) {
        if(user != null && isMyDestination && ! existsByUser(user)){
            LOGGER.warn("User has no journeys");
            throw new UserHasNoJourneyException(user.getId());
        }

        LocalDate adjustedStartDate = startDate;
        LocalDate adjustedEndDate = endDate;
        LocalDate today = LocalDate.now();

        if (isOngoing) {
            adjustedStartDate = startDate == null || startDate.isAfter(today) ? today : startDate;
            adjustedEndDate = endDate == null || endDate.isBefore(today) ? today : endDate;
        } else if (isUpcoming) {
            adjustedStartDate = ensureStartDateForUpcomingJourneys(startDate);
        } else if (isPast) {
            adjustedEndDate = capEndDateForPastJourneys(endDate);
        }

        return journeyDao.search(
                search,
                user != null ? user.getId() : null,
                sortBy,
                direction,
                destination,
                adjustedStartDate,
                adjustedEndDate,
                interest,
                isMyDestination,
                pageParams
        );

    }



    @Override
    public boolean existsByUserEmail(final String email) {
        LOGGER.debug("Checking if user has journey {}", email);
        User user = userService.findUserByEmail(email).orElseThrow(() -> {
            LOGGER.warn("User with email '{}' not found", email);
            return new UserNotFoundException(email);
        });
        return user.getJourney() != null;
    }

    @Override
    public boolean existsByUser(final User user) {
        LOGGER.debug("Checking if user has journey {}", user);
        return (user.getJourney() != null) && (!user.getJourney().isDeleted());
    }


    @Override
    public List<Journey> findRecommendedJourneys(final String email, final int limit) {
        LOGGER.debug("Getting recommended journeys for {}", email);
        if(limit <= 0 ){
            throw new InvalidPaginationParamsException("Limit must be grater than 0");
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

        Optional<Journey> maybeJourney = journeyDao.findById(id);
        if (maybeJourney.isEmpty()) {
            LOGGER.info("Journey with id {} not found", id);
            return;
        }
        Journey journey = maybeJourney.get();

        LOGGER.info("Journey deletion message updated: {}", message);
        if(message != null && ! message.isEmpty()){
            journey.setDeletionMessage(message);
        }

        emailService.sendJourneyDeletionNotification(new EmailJourney(journey),message);
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
    public Journey updateJourney(final long journeyId, final  String destinationUniversity, final LocalDate startDate, final LocalDate endDate, final String description) {
        LOGGER.debug("Editing journey {}", journeyId);
        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey with id {} not found", journeyId);
                    return new JourneyNotFoundException(journeyId);
                });
        University university = universityService.findByName(destinationUniversity)
                .orElseThrow(() -> {
                    LOGGER.warn("University not found: {}", destinationUniversity);
                    return new UniversityNotFoundException(destinationUniversity);
                });
        journey.setDestinationUniversity(university);
        journey.setStartDate(startDate);
        journey.setEndDate(endDate);
        journey.setDescription(description);
        LOGGER.info("Journey updated: {}", journeyId);
        return journey;
    }


    @Override
    public Optional<JourneyResponse> findJourneyResponseById(final long id) {
        LOGGER.debug("Finding journey response by id {}", id);
        return journeyResponseDao.findById(id);
    }

    @Override
    @Transactional
    public void deleteJourneyResponse(final long id, final String message) {
        LOGGER.debug("Deleting journey response {}", id);
        Optional<JourneyResponse> maybeJourneyResponse = findJourneyResponseById(id);
        if (maybeJourneyResponse.isEmpty()) {
            LOGGER.info("Journey response with id {} not found", id);
            return;
        }
        JourneyResponse journeyResponse = maybeJourneyResponse.get();

        User commentAuthor = journeyResponse.getUser();

        emailService.sendJourneyCommentDeletionNotification(journeyResponse, new EmailJourney(journeyResponse.getJourney()), new EmailUser(commentAuthor), message);
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
    public Page<Tip> findTipsByJourney(Journey journey, PageParams pageParams) {
        LOGGER.debug("Finding tips for journey {}", journey);
        return tipDao.findByJourney(journey, pageParams);
    }

    @Override
    @Transactional
    public Tip createTip(long journeyId, String title, String content) {
        Journey journey = journeyDao.findById(journeyId).orElseThrow(() -> {
            LOGGER.error("Journey with id {} not found", journeyId);
            return new JourneyNotFoundException(journeyId);
        });
         return tipDao.create(journey, title, content);
    }


    @Override
    @Transactional
    public Tip updateTip(long tipId, String title, String content) {
        Tip tip = findTipById(tipId).orElseThrow(() -> {
            LOGGER.error("Tip with id {} not found", tipId);
            return new TipNotFoundException(tipId);
        });
        tip.setTitle(title);
        tip.setContent(content);
        LOGGER.info("Tip updated: {}", tipId);
        return tip;
    }

    @Override
    @Transactional
    public void deleteTip(long tipId) {
        tipDao.delete(tipId);
    }

    @Override
    public Optional<Tip> findTipById(long tipId) {
        LOGGER.debug("Finding tip by id {}", tipId);
        return tipDao.findById(tipId);
    }


    @Override
    public boolean isTipOwnedByUser(long tipId, String email) {
        Tip tip = findTipById(tipId).orElseThrow(() -> {
            LOGGER.error("Tip with id {} not found", tipId);
            return new TipNotFoundException(tipId);
        });
        return tip.getJourney().getUser().getEmail().equals(email);
    }
}
