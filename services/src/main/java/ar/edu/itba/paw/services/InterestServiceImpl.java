package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class InterestServiceImpl implements InterestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterestServiceImpl.class);
    private final InterestDao interestDao;
    @Autowired
    public InterestServiceImpl(final InterestDao interestDao) {
        this.interestDao = interestDao;
    }

    @Override
    @Cacheable(value = "interestsById", key = "#id")
    public Optional<Interest> findById(final long id) {
        LOGGER.debug("Getting interest {}", id);
        return this.interestDao.findById(id);
    }


    @Override
    public List<Interest> findByUserId(final long id) {
        LOGGER.debug("Getting interests of user {}", id);
        return interestDao.findAllByUserId(id);
    }

    @Override
    @Cacheable(value = "interestsByName", key = "#name")
    public Optional<Interest> findByName(final String name) {
        LOGGER.debug("Getting interest {}", name);
        return interestDao.findByName(name);
    }


    @Override
    public Page<Interest> findAllInterestsByUserId(final long id, final PageParams pageParams) {
        LOGGER.debug("Getting interests of user {} with pageParams {}", id, pageParams);
        return interestDao.findAllByUserId(id, pageParams);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                @CachePut(value = "interestsByName", key = "#name"),
                @CachePut(value = "interestsById", key = "#result.id")
            }
    )
    public Interest createUserInterest(final String name) {
        LOGGER.debug("Creating interest {}", name);
        Interest interest = interestDao.create(name);
        LOGGER.info("Interest {} created", interest);
        return interest;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interestsById", key = "#id"),
            @CacheEvict(value = "interestsByName", allEntries = true)
    })
    @Override
    public void editUserInterest(final long id, String interest) {
        LOGGER.debug("Editing interest {} with name {}", id, interest);
        interestDao.update(id, interest);
        LOGGER.info("Interest {} updated", id);
    }

    @Override
    @Transactional
    public void saveUserInterests(final long[] interests,final  long userId) {
        LOGGER.debug("Adding interest list to user {}", userId);
        interestDao.createUserInterests(interests, userId);
        LOGGER.info("Interests {} added to user {}", interests, userId);
    }

    @Override
    public void saveUserInterests(final List<String> interests,final  long userId) {
        LOGGER.debug("Adding interest list to user {}", userId);
        interestDao.createUserInterests(interests, userId);
        LOGGER.info("Interests {} added to user {}", interests, userId);
    }

    @Override
    @Transactional
    public void updateScoreByInterest(final Interest interest,final long userId) {
        LOGGER.debug("Increasing score of interest {} for user {}", interest, userId);
        interestDao.updateScoreByInterest(interest, userId);
        LOGGER.info("Interest {} score updated for user {}", interest, userId);
    }

    @Override
    @Transactional
    public void updateScoreByInterests(final List<Interest> interests,final long userId) {
        LOGGER.debug("Increasing score of interests {} for user {}", interests, userId);
        interestDao.updateScoreByInterests(interests, userId);
        LOGGER.info("Interests {} score updated for user {}", interests, userId);
    }

    @Override
    @Transactional
    public void updateUserInterests(final long[] interestIds, final long userId) {
        LOGGER.debug("Updating interests {} for user {}", interestIds, userId);
        interestDao.updateUserInterests(interestIds, userId);
        LOGGER.info("Interests {} updated for user {}", interestIds, userId);
    }

    @Override
    public Page<Interest> getAllInterests(final String search,final  PageParams pageParams) {
        LOGGER.debug("Finding all interests with search {}", search);
        if (search == null || search.isEmpty()) {
            return interestDao.findAll(pageParams);
        }
        return interestDao.search(search,pageParams);
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interestsById", key = "#id"),
            @CacheEvict(value = "interestsByName", allEntries = true)
    })
    public void delete(final long id) {
        LOGGER.debug("Deleting interest {}", id);
        interestDao.delete(id);
        LOGGER.info("Interest {} deleted", id);
    }


}
