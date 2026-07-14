package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.persistence.ReportDao;
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

import static ar.edu.itba.paw.services.AfterCommitExecutor.runAfterCommit;
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
    private final ReportDao reportDao;
    private final UserService userService;
    private final EmailService emailService;
    private final UniversityService universityService;
    private final InterestService interestService;


    @Autowired
    public JourneyServiceImpl(final JourneyDao journeyDao, final UserService userService,
                              final UniversityService universityService, final JourneyResponseDao journeyResponseDao, TipDao tipDao, final EmailService emailService, final InterestService interestService, final ReportDao reportDao) {
        this.journeyDao = journeyDao;
        this.userService = userService;
        this.universityService = universityService;
        this.journeyResponseDao = journeyResponseDao;
        this.tipDao = tipDao;
        this.emailService = emailService;
        this.interestService = interestService;
        this.reportDao = reportDao;
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
    public Journey createJourney(final long userId, final long destinationUniversityId, final LocalDate startDate, final LocalDate endDate, final String description) {
        LOGGER.debug("Creating journey for user {}", userId);

        User user = userService.findUserById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        checkDates(startDate, endDate);
        University destination = universityService.findById(destinationUniversityId)
                .orElseThrow(() -> {
                    LOGGER.warn("Destination university not found: {}", destinationUniversityId);
                    return new InvalidReferenceException("University", destinationUniversityId);
                }
        );

        Journey existingJourney = user.getJourney();
        if (existingJourney != null) {
            if (!existingJourney.isDeleted()) {
                LOGGER.warn("User {} already has an active journey", userId);
                throw new UserWithActiveJourneyException(userId);
            }
            // Hard delete the soft-deleted journey and its responses
            LOGGER.info("Hard deleting previous journey {} and its responses for user {}", existingJourney.getId(), userId);
            reportDao.hardDeleteByJourneyId(existingJourney.getId());
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
    public JourneyResponse createJourneyResponse(final long userId, final long journeyId, final String message) {
        LOGGER.debug("Replying to journey {} by user {}", journeyId, userId);
        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Journey with id {} not found", journeyId);
                    return new JourneyNotFoundException(journeyId);
                });

        User responder = userService.findUserById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
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
                List<EmailUser> respondersSnapshot = List.copyOf(responders);
                runAfterCommit(() -> emailService.answerJourneyNotification(
                        respondersSnapshot,
                        message,
                        emailResponder,
                        emailJourney
                ));
            }

            page++;
        } while (page <= respondersPage.getTotalPages());
        LOGGER.info("Journey response notifications sent to all responders for journey {}", journeyId);

        runAfterCommit(() -> emailService.answerJourneyOwnerNotification(message, emailResponder, emailJourney));
        LOGGER.info("Journey response notifications sent to owner for journey {}", journeyId);
        return journeyResponse;
    }

    private Page<Journey> searchByTerm(final String searchTerm, final PageParams pageParams){
        return journeyDao.search(
                searchTerm,
                null,   // excludeUserId
                null,   // destinationCityId
                null,   // orderBy
                null,   // direction
                null,   // city
                null,   // university
                null,   // startDate
                null,   // endDate
                null,   // interest
                false,  // isUpcoming
                false,  // isPast
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

    private void validateMutuallyExclusiveTimeFilters(boolean isPast, boolean isUpcoming, boolean isOngoing) {
        int count = (isPast ? 1 : 0) + (isUpcoming ? 1 : 0) + (isOngoing ? 1 : 0);
        if (count > 1) {
            throw new MutuallyExclusiveFiltersException("past", "upcoming", "ongoing"); // ¿Es correcto mandar esto?
        }
    }


    @Override
    public Page<Journey> findJourneys(final String search, final Long excludeUserId, final Long destinationCityId, final SortFieldJourney sortBy, final SortDirection direction, final String city,
                                      final String university, final LocalDate startDate, final LocalDate endDate, final String interest,
                                      final boolean isPast, final boolean isUpcoming, final boolean isOngoing,
                                      final PageParams pageParams) {
        return findJourneys(search, null, excludeUserId, destinationCityId, sortBy, direction, city, university, startDate, endDate, interest, isPast, isUpcoming, isOngoing, pageParams);
    }

    @Override
    public Page<Journey> findJourneys(final String search, final Long recommendedForUser, final Long excludeUserId, final Long destinationCityId, final SortFieldJourney sortBy, final SortDirection direction, final String city,
                                      final String university, final LocalDate startDate, final LocalDate endDate, final String interest,
                                      final boolean isPast, final boolean isUpcoming, final boolean isOngoing,
                                      final PageParams pageParams) {
        if (recommendedForUser != null) {
            validateRecommendedJourneysFilters(search, excludeUserId, destinationCityId, sortBy, direction, city, university, startDate, endDate, interest, isPast, isUpcoming, isOngoing);
            return findRecommendedJourneys(recommendedForUser, pageParams);
        }

        validateMutuallyExclusiveTimeFilters(isPast, isUpcoming, isOngoing);

        final SortFieldJourney effectiveSortBy = sortBy == null ? SortFieldJourney.START_DATE : sortBy;
        final SortDirection effectiveDirection = direction == null ? SortDirection.ASC : direction;

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
                excludeUserId,
                destinationCityId,
                effectiveSortBy,
                effectiveDirection,
                city,
                university,
                adjustedStartDate,
                adjustedEndDate,
                interest,
                isUpcoming,
                isPast,
                pageParams
        );

    }

    private void validateRecommendedJourneysFilters(final String search, final Long excludeUserId, final Long destinationCityId, final SortFieldJourney sortBy, final SortDirection direction,
                                                    final String city, final String university, final LocalDate startDate, final LocalDate endDate, final String interest,
                                                    final boolean isPast, final boolean isUpcoming, final boolean isOngoing) {
        if (city != null || university != null || startDate != null || endDate != null || interest != null
                || isPast || isUpcoming || isOngoing || destinationCityId != null || excludeUserId != null
                || search != null || sortBy != null || direction != null) {
            throw new MutuallyExclusiveFiltersException("recommendedForUser");
        }
    }



    private Page<Journey> findRecommendedJourneys(final long userId, final PageParams pageParams) {
        LOGGER.debug("Getting recommended journeys for user {}", userId);

        final User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.warn("User with id {} not found", userId);
            return new UserNotFoundException(userId);
        });

        if (user.hasActiveJourney()) {
            return journeyDao.findRecommended(userId, pageParams);
        }

        final Page<Journey> journeysFromOriginCity = journeyDao.findByOriginCity(
                user.getUniversity().getCity().getId(),
                pageParams
        );
        if (!journeysFromOriginCity.getContent().isEmpty()) {
            return journeysFromOriginCity;
        }

        return journeyDao.findAll(pageParams);
    }
    @Override
    @Transactional
    public Journey patchJourney(final long id, final Long destinationUniversityId, final LocalDate startDate,
                                final LocalDate endDate, final String description,
                                final Boolean deleted, final String deletionMessage) {
        LOGGER.debug("Patching journey {}", id);
        Journey journey = journeyDao.findById(id).orElseThrow(() -> new JourneyNotFoundException(id));

        if (destinationUniversityId != null) {
            University university = universityService.findById(destinationUniversityId).orElseThrow(() -> new InvalidReferenceException("University", destinationUniversityId));
            journey.setDestinationUniversity(university);
        }
        if (startDate != null) {
            journey.setStartDate(startDate);
        }
        if (endDate != null) {
            journey.setEndDate(endDate);
        }
        if (description != null) {
            journey.setDescription(description);
        }
        if (Boolean.TRUE.equals(deleted)) {
            deleteJourney(id, deletionMessage);
        }

        LOGGER.info("Journey patched: {}", id);
        return journey;
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

        runAfterCommit(() -> emailService.sendJourneyDeletionNotification(new EmailJourney(journey), message));
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
    public Optional<JourneyResponse> findJourneyResponseById(final long id) {
        LOGGER.debug("Finding journey response by id {}", id);
        return journeyResponseDao.findById(id);
    }

    @Override
    public boolean isJourneyResponseOwnedByUser(final long journeyId, final long responseId, final long userId) {
        LOGGER.debug("Checking if response {} for journey {} is owned by user {}", responseId, journeyId, userId);
        Optional<JourneyResponse> response = findJourneyResponseById(journeyId, responseId);
        return response.isPresent() && response.get().getUser().getId() == userId;
    }

    @Override
    public Optional<JourneyResponse> findJourneyResponseById(final long journeyId, final long responseId) {
        Optional<JourneyResponse> maybeResponse = journeyResponseDao.findById(responseId);
        if (maybeResponse.isPresent() && maybeResponse.get().getJourney().getId() != journeyId) {
            LOGGER.warn("Journey response {} does not belong to journey {}", responseId, journeyId);
            return Optional.empty();
        }
        return maybeResponse;
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

        runAfterCommit(() -> emailService.sendJourneyCommentDeletionNotification(journeyResponse, new EmailJourney(journeyResponse.getJourney()), new EmailUser(commentAuthor), message));
        LOGGER.info("Journey response deletion notification sent to user {}", commentAuthor.getEmail());

        journeyResponse.setDeletionMessage(message);
        LOGGER.info("Journey response deletion message updated: {}", message);

        journeyResponse.setDeleted(true);
        LOGGER.info("Journey response deleted: {}", id);
    }

    @Override
    @Transactional
    public void deleteJourneyResponse(final long journeyId, final long responseId, final String message) {
        LOGGER.debug("Deleting journey response {} for journey {}", responseId, journeyId);
        findJourneyResponseById(journeyId, responseId).orElseThrow(() -> new JourneyResponseNotFoundException(journeyId, responseId));
        deleteJourneyResponse(responseId, message);
    }

    @Override
    @Transactional
    public void patchJourneyResponse(final long journeyId, final long responseId, final Boolean deleted, final String deletionMessage) {
        LOGGER.debug("Patching journey response {} for journey {}", responseId, journeyId);

        if (Boolean.TRUE.equals(deleted)) {
            deleteJourneyResponse(journeyId, responseId, deletionMessage);
        }
    }



    @Override
    public Page<JourneyResponse> findJourneyResponses(final long journeyId, final PageParams pageParams) {
        LOGGER.debug("Finding all journey responses for journey {}", journeyId);
        journeyDao.findById(journeyId).orElseThrow(() -> {
            LOGGER.warn("Journey with id {} not found", journeyId);
            return new JourneyNotFoundException(journeyId);
        });
        return journeyResponseDao.findAllByJourneyId(journeyId, pageParams);
    }

    @Override
    public Page<Tip> findTipsByJourneyId(long journeyId, PageParams pageParams) {
        LOGGER.debug("Finding tips for journey {}", journeyId);
        journeyDao.findById(journeyId).orElseThrow(() -> {
            LOGGER.warn("Journey with id {} not found", journeyId);
            return new JourneyNotFoundException(journeyId);
        });
        return tipDao.findByJourneyId(journeyId, pageParams);
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
    public Tip patchTip(long journeyId, long tipId, String title, String content) {
        LOGGER.debug("Patching tip {} for journey {}", tipId, journeyId);
        Tip tip = findTipById(journeyId, tipId).orElseThrow(() -> new TipNotFoundException(journeyId, tipId));

        if (title != null) {
            tip.setTitle(title);
        }
        if (content != null) {
            tip.setContent(content);
        }

        LOGGER.info("Tip patched: {}", tipId);
        return tip;
    }

    @Override
    @Transactional
    public void deleteTip(long journeyId, long tipId) {
        findTipById(journeyId, tipId).orElseThrow(() -> new TipNotFoundException(journeyId, tipId));
        tipDao.delete(tipId);
    }

    @Override
    public Optional<Tip> findTipById(long tipId) {
        LOGGER.debug("Finding tip by id {}", tipId);
        return tipDao.findById(tipId);
    }

    @Override
    public Optional<Tip> findTipById(long journeyId, long tipId) {
        LOGGER.debug("Finding tip by id {} for journey {}", tipId, journeyId);
        Optional<Tip> maybeTip = tipDao.findById(tipId);
        if (maybeTip.isPresent() && maybeTip.get().getJourney().getId() != journeyId) {
            LOGGER.warn("Tip {} does not belong to journey {}", tipId, journeyId);
            return Optional.empty();
        }
        return maybeTip;
    }


    @Override
    public boolean isTipOwnedByUser(long journeyId, long tipId, String email) {
        Tip tip = findTipById(journeyId, tipId).orElseThrow(() -> new TipNotFoundException(journeyId, tipId));
        return tip.getJourney().getUser().getEmail().equals(email);
    }
}
