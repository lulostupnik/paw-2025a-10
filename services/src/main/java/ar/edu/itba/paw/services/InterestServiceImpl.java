package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.persistence.UserInterestDao;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class InterestServiceImpl implements InterestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterestServiceImpl.class);
    private final InterestDao interestDao;
    private final UserInterestDao userInterestDao;
    @Autowired
    public InterestServiceImpl(final InterestDao interestDao, final UserInterestDao userInterestDao) {
        this.userInterestDao = userInterestDao;
        this.interestDao = interestDao;
    }

    @Override
    public Optional<Interest> findInterestById(final long id) {
        LOGGER.debug("Getting interest {}", id);
        return this.interestDao.findById(id);
    }


    @Override
    public List<UserInterest> findInterestsByUser(final User user) {
        LOGGER.debug("Getting interests of user {}", user);

        return userInterestDao.findAllByUser(user);
    }
    @Override
    @Transactional
    public void updateUserInterestScores(List<UserInterest> interests){
        for(UserInterest interest : interests){
            interest.setScore(interest.getScore()+1);
        }
    }

    @Override
    public Optional<Interest> findInterestByName(final String name) {
        LOGGER.debug("Getting interest {}", name);
        return interestDao.findByName(name);
    }


    @Override
    public Page<UserInterest> findInterestsByUser(final User user, final PageParams pageParams) {
        LOGGER.debug("Getting interests of user {} with pageParams {}", user, pageParams);
        return userInterestDao.findAllByUser(user, pageParams);
    }

    @Override
    @Transactional
    public Interest createInterest(final String name) {
        LOGGER.debug("Creating interest {}", name);
        Interest interest = interestDao.create(name);
        LOGGER.info("Interest {} created", interest);
        return interest;
    }

    @Override
    @Transactional
    public void updateInterest(final long id, String interest) {
        LOGGER.debug("Editing interest {} with name {}", id, interest);
        Interest i = interestDao.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Interest with id {} not found", id);
                    return new InterestsNotFoundException(id);
                });
        i.setName(interest);

        LOGGER.info("Interest {} updated", id);
    }

    @Override
    @Transactional
    public void createUserInterests(final List<String> interests, final  long userId) {
        LOGGER.debug("Adding interest list to user {}", userId);
        userInterestDao.createUserInterests(interests, userId);
        LOGGER.info("Interests {} added to user {}", interests, userId);
    }

    @Override
    @Transactional
    public void updateUserInterests(final long[] interestIds, final long userId) {
        LOGGER.debug("Updating interests {} for user {}", interestIds, userId);
        userInterestDao.updateUserInterests(interestIds, userId);
        LOGGER.info("Interests {} updated for user {}", interestIds, userId);
    }

    @Override
    public Page<Interest> findInterests(final String search, final  PageParams pageParams) {
        LOGGER.debug("Finding all interests with search {}", search);
        if (search == null || search.isEmpty()) {
            return interestDao.findAll(pageParams);
        }
        return interestDao.search(search,pageParams);
    }


    @Override
    @Transactional
    public void updateMatchingInterestScores(long responderUserId, long journeyCreatorUserId) {
        LOGGER.debug("Updating matching interest scores for responder {} based on journey creator {}",
                responderUserId, journeyCreatorUserId);
        userInterestDao.updateMatchingInterestScores(responderUserId, journeyCreatorUserId);
        LOGGER.info("Updated matching interest scores for responder {}", responderUserId);
    }


    @Override
    @Transactional
    public void deleteInterest(final long id) {
        interestDao.delete(id);
        LOGGER.info("Interest {} deleted", id);
    }


}
