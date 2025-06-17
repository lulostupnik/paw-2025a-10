package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CareerServiceImpl implements CareerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CareerServiceImpl.class);

    private final CareerDao careerDao;

    @Autowired
    public CareerServiceImpl(CareerDao careerDao) {
        this.careerDao = careerDao;
    }

    @Override
    @Cacheable(value = "careersById", key = "#id")
    public Optional<Career> findCareerById(final long id) {
        LOGGER.debug("Getting career by id {}", id);
        return careerDao.findById(id);
    }



    @Override
    @Cacheable(value = "careersByName", key = "#name")
    public Optional<Career> findCareerByName(final String name) {
        LOGGER.debug("Getting career by name {}", name);
        return careerDao.findByName(name);
    }

    @Override
    public Page<Career> searchCareers(final String search, final PageParams pageParams) {
        LOGGER.debug("Getting all careers with search {}", search);
        if (search == null || search.isEmpty()) {
            return careerDao.findAll(pageParams);
        }
        return careerDao.search(search, pageParams);
    }

    @Override
    @Transactional
    @Caching(put = {
                    @CachePut(value = "careersById", key = "#result.id"),
                    @CachePut(value = "careersByName", key = "#result.name")
    })
    public Career createCareer(final String name) {
        LOGGER.debug("Creating career {}", name);
        Career career = careerDao.create(name);
        LOGGER.info("Career {} created", name);
        return career;
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "careersById", key = "#id"),
                    @CacheEvict(value = "careersByName", allEntries = true)
            }
    )
    public Career updateCareer(final long id, final String name) {
        LOGGER.debug("Updating career {} to {}", id, name);
        Career career = careerDao.findById(id).orElseThrow(() -> {
            LOGGER.error("Career not found with id: {}", id);
            return new CareerNotFoundException(id);
        });
        career.setName(name);
        LOGGER.info("Career {} updated to {}", id, name);
        return career;
    }

    @Override
    @Transactional
    @Caching(evict = {
                @CacheEvict(value = "careersById", key = "#id"),
                @CacheEvict(value = "careersByName", allEntries = true)
    })
    public void deleteCareer(final long id) {
        Optional<Career> maybeCareer = careerDao.findById(id);
        if (maybeCareer.isEmpty()) {
            LOGGER.info("Career {} not found", id);
            return;
        }
        maybeCareer.get().setDeleted(true);
        LOGGER.info("Career {} deleted", id);
    }


}
