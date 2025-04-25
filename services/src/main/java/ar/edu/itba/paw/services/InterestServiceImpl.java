package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Interest;

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
    
    @Autowired
    public InterestServiceImpl(InterestDao interestDao) {
        this.interestDao = interestDao;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "interestsById", key = "#id")
    @Override
    public Optional<Interest> findById(Long id) {
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
    public List<Interest> findByUserId(Long id) {
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
    public Optional<Interest> createUserInterest(Interest interest, Long userId) {
        LOGGER.debug("Adding interest {} to user {}", interest, userId);
        LOGGER.warn("NOT IMPLEMENTED");
        return Optional.empty();
    }

    @Transactional
    @Override
    public List<Interest> createUserInterests(String[] interests, Long userId) {
        LOGGER.debug("Adding interest list to user {}", userId);
        return interestDao.createUserInterests(interests, userId);
    }

    @Transactional
    @Override
    public void updateScoreByInterest(Interest interest, Long userId) {
        LOGGER.debug("Increasing score of interest {} for user {}", interest, userId);
        interestDao.updateScoreByInterest(interest, userId);
    }

    @Transactional
    @Override
    public void updateScoreByInterests(List<Interest> interests, Long userId) {
        LOGGER.debug("Increasing score of interests {} for user {}", interests, userId);
        interestDao.updateScoreByInterests(interests, userId);

    }


}
