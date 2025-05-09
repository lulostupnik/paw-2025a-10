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
    private static final int DEFAULT_PAGE_SIZE = 30;


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
    public List<Interest> findIdByName(List<String> names) {
        LOGGER.debug("Getting interests from name list");
        return interestDao.findIdByName(names);
    }

    @Override
    public Page<Interest> findAllInterestsByUserId(long id, PageParams pageParams) {
        return interestDao.findAllInterestsByUserId(id, pageParams);
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

    @Override
    @Transactional
    public void updateScoreByInterests(List<Interest> interests, long userId) {
        LOGGER.debug("Increasing score of interests {} for user {}", interests, userId);
        interestDao.updateScoreByInterests(interests, userId);
        // FIXME: OJO!, CREO QUE EL INTEREST DAO NO PUEDE TOCAR LA TABLA DE USER
        // -> esto debería estar en el user dao

    }

    @Override
    @Transactional(readOnly = false)
    public void updateUserInterests(long[] interestIds, long userId) {
        interestDao.updateUserInterests(interestIds, userId);
    }

    @Override
    public Page<Interest> getAllInterests(String search, PageParams pageParams) {
        LOGGER.debug("Finding all interests with search {}", search);
        if (search == null || search.isEmpty()) {
            return interestDao.getAllInterests(pageParams);
        }
        return interestDao.searchBySubstring(search,pageParams);
    }

    @Override
    public String getInterestsJSON(String search, PageParams pageParams) {
        if(search == null || search.isEmpty()) {
            List<Interest> interests = interestDao.getAllInterests(pageParams).getContent();
            return listToJson(interests);
        }
        List<Interest> interests = interestDao.searchBySubstring(search,pageParams).getContent();
        return listToJson(interests);
    }

    private String listToJson(List<Interest> interests) {
        StringBuilder json = new StringBuilder("[");
        for (Interest interest : interests) {
            json.append(interest.toJSON()).append(",");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // Remove last comma
        }
        json.append("]");
        LOGGER.debug("JSON interests: {}", json);
        return json.toString();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interestsById", key = "#id"),
            @CacheEvict(value = "interests", allEntries = true),
            @CacheEvict(value = "interestsByName", allEntries = true)
    })
    public void delete(long id) {
        interestDao.delete(id);
    }


}
