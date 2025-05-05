package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.Interest;

import ar.edu.itba.paw.models.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InterestServiceImpl implements InterestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterestServiceImpl.class);

    private final InterestDao interestDao;
    private static final int DEFAULT_PAGE_SIZE = 30;


    @Autowired
    public InterestServiceImpl(InterestDao interestDao) {
        this.interestDao = interestDao;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "interestsById", key = "#id")
    @Override
    public Optional<Interest> findById(long id) {
        LOGGER.debug("Getting interest {}", id);
        return this.interestDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "interests", unless = "#result.size() > 100")
    @Override
    public List<Interest> findAll() {
        LOGGER.debug("Getting all interests");
        return interestDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Interest> findByUserId(long id) {
        LOGGER.debug("Getting interests of user {}", id);
        return interestDao.findByUserId(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "interestsByName", key = "#name")
    @Override
    public Optional<Interest> findByName(String name) {
        LOGGER.debug("Getting interest {}", name);
        return interestDao.findByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Interest> findIdByName(String[] names) {
        LOGGER.debug("Getting interests from name list");
        return interestDao.findIdByName(names);
    }

    @Transactional
    @Override
    public Interest createUserInterest(String interest) {
        return interestDao.createUserInterest(interest);
    }


    @Transactional
    @Override
    public void deleteUserInterest(long id) {
        interestDao.deleteUserInterest(id);
    }

    @Transactional
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

    @Transactional
    @Override
    public void saveUserInterests(String[] interests, long userId) {
        List<Interest> interestList = interestDao.findIdByName(interests);
        long[] interestIds = new long[interestList.size()];
        for (int i = 0; i < interestList.size(); i++) {
            interestIds[i] = interestList.get(i).getId();
        }
        LOGGER.debug("Adding interest list to user {}", userId);
        interestDao.saveUserInterests(interestIds, userId);
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
    public Page<Interest> getAllInterests(String search, int page, int pageSize) {
        LOGGER.debug("Finding all interests with search {}", search);
        if (search == null || search.isEmpty()) {
            return interestDao.getAllInterests(page, pageSize);
        }
        return interestDao.searchBySubstring(search,page, pageSize);
    }

    @Override
    public String getInterestsJSON(String search) {
        if(search == null || search.isEmpty()) {
            List<Interest> interests = interestDao.getAllInterests(1,DEFAULT_PAGE_SIZE).getContent();
            return listToJson(interests);
        }
        List<Interest> interests = interestDao.searchBySubstring(search,1,DEFAULT_PAGE_SIZE).getContent();
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
    public void delete(long id) {
        interestDao.delete(id);
    }


}
