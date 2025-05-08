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
    public InterestServiceImpl(InterestDao interestDao) {
        this.interestDao = interestDao;
    }

    @Cacheable(value = "interestsById", key = "#id")
    @Override
    public Optional<Interest> findById(long id) {
        LOGGER.debug("Getting interest {}", id);
        return this.interestDao.findById(id);
    }

    @Cacheable(value = "interests", unless = "#result.size() > 100")
    @Override
    public List<Interest> findAll() {
        LOGGER.debug("Getting all interests");
        return interestDao.findAll();
    }

    @Override
    public List<Interest> findByUserId(long id) {
        LOGGER.debug("Getting interests of user {}", id);
        return interestDao.findByUserId(id);
    }

    @Cacheable(value = "interestsByName", key = "#name")
    @Override
    public Optional<Interest> findByName(String name) {
        LOGGER.debug("Getting interest {}", name);
        return interestDao.findByName(name);
    }

    @Override
    public List<Interest> findIdByName(String[] names) {
        LOGGER.debug("Getting interests from name list");
        return interestDao.findIdByName(names);
    }

    @Override
    public Page<Interest> findAllInterestsByUserId(long id, PageParams pageParams) {
        return interestDao.findAllInterestsByUserId(id, pageParams.getPage(), pageParams.getSize());
    }

    @Transactional
    @Override
    @CacheEvict(value = "interests", allEntries = true)
    public Interest createUserInterest(String interest) {
        return interestDao.createUserInterest(interest);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interestsById", key = "#id"),
            @CacheEvict(value = "interests", allEntries = true),
            @CacheEvict(value = "interestsByName", allEntries = true)
    })
    @Override
    public void deleteUserInterest(long id) {
        interestDao.deleteUserInterest(id);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interestsById", key = "#id"),
            @CacheEvict(value = "interests", allEntries = true),
            @CacheEvict(value = "interestsByName", allEntries = true)
    })
    @Override
    public void editUserInterest(long id, String interest) {
        interestDao.editUserInterest(id, interest);
    }

    @Transactional
    @Override
    public void saveUserInterests(long[] interests, long userId) {
        LOGGER.debug("Adding interest list to user {}", userId);
        interestDao.saveUserInterests(interests, userId);
    }

    @Override
    public void saveUserInterests(List<String> interests, long userId) {
        interestDao.saveUserInterests(interests, userId);
    }

    @Transactional
    @Override
    public void updateScoreByInterest(Interest interest, long userId) {
        LOGGER.debug("Increasing score of interest {} for user {}", interest, userId);
        interestDao.updateScoreByInterest(interest, userId);
    }

    @Transactional
    @Override
    public void updateScoreByInterests(List<Interest> interests, long userId) {
        LOGGER.debug("Increasing score of interests {} for user {}", interests, userId);
        interestDao.updateScoreByInterests(interests, userId);

    }

    @Override
    public Page<Interest> getAllInterests(String search, PageParams pageParams) {
        LOGGER.debug("Finding all interests with search {}", search);
        if (search == null || search.isEmpty()) {
            return interestDao.getAllInterests(pageParams.getPage(), pageParams.getSize());
        }
        return interestDao.searchBySubstring(search,pageParams.getPage(), pageParams.getSize());
    }

    @Override
    public void deleteUserInterests(long userId, long[] interests) {
        LOGGER.debug("Deleting interests {} from user {}", interests, userId);
        for(long interest : interests) {
            interestDao.deleteUserInterest(interest);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interestsById", key = "#id"),
            @CacheEvict(value = "interests", allEntries = true),
            @CacheEvict(value = "interestsByName", allEntries = true)
    })
    @Override
    public void delete(long id) {
        interestDao.delete(id);
    }


}
