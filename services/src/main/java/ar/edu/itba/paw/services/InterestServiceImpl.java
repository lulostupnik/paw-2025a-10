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

//                 "interests", "interestsById", "interestsByName",

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
    public Page<Interest> findAllInterestsByUserId(final long id, PageParams pageParams) {
        return interestDao.findAllByUserId(id, pageParams);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                @CachePut(value = "interestsByName", key = "#name"),
                @CachePut(value = "interestsById", key = "#result.id")
            },
            evict = {
                @CacheEvict(value = "interests", allEntries = true)
            }
    )
    public Interest createUserInterest(final String name) {
        return interestDao.create(name);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "interestsById", key = "#id"),
            @CacheEvict(value = "interests", allEntries = true),
            @CacheEvict(value = "interestsByName", allEntries = true)
    })
    @Override
    public void editUserInterest(final long id, String interest) {
        interestDao.update(id, interest);
    }

    @Override
    @Transactional
    public void saveUserInterests(final long[] interests,final  long userId) {
        LOGGER.debug("Adding interest list to user {}", userId);
        interestDao.createUserInterests(interests, userId);
    }

    @Override
    public void saveUserInterests(final List<String> interests,final  long userId) {
        interestDao.createUserInterests(interests, userId);
    }

    @Override
    @Transactional
    public void updateScoreByInterest(final Interest interest,final long userId) {
        LOGGER.debug("Increasing score of interest {} for user {}", interest, userId);
        interestDao.updateScoreByInterest(interest, userId);
    }

    @Override
    @Transactional
    public void updateScoreByInterests(final List<Interest> interests,final long userId) {
        LOGGER.debug("Increasing score of interests {} for user {}", interests, userId);
        interestDao.updateScoreByInterests(interests, userId);
        // FIXME: OJO!, CREO QUE EL INTEREST DAO NO PUEDE TOCAR LA TABLA DE USER
        // -> esto debería estar en el user dao
        //@TODO
    }

    @Override
    @Transactional
    public void updateUserInterests(final long[] interestIds, final long userId) {
        interestDao.updateUserInterests(interestIds, userId);
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
    public String getInterestsJSON(final String search,final  PageParams pageParams) {
        if(search == null || search.isEmpty()) {
            List<Interest> interests = interestDao.findAll(pageParams).getContent();
            return listToJson(interests);
        }
        List<Interest> interests = interestDao.search(search,pageParams).getContent();
        return listToJson(interests);
    }

    private String listToJson(final List<Interest> interests) {
        StringBuilder json = new StringBuilder("[");
        for (Interest interest : interests) {
            json.append(interest.toJSON()).append(", ");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // Remove last space
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
    public void delete(final long id) {
        interestDao.delete(id);
    }


}
