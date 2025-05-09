package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
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
public class CareerServiceImpl implements CareerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CareerServiceImpl.class);

    private final CareerDao careerDao;

    @Autowired
    public CareerServiceImpl(CareerDao careerDao) {
        this.careerDao = careerDao;
    }

    @Override
    @Cacheable(value = "careersById", key = "#id")
    public Optional<Career> findById(long id) {
        LOGGER.debug("Getting career by id {}", id);
        return careerDao.findById(id);
    }

    @Override
    public List<Career> findAll() {
        LOGGER.debug("Getting all careers");
        return careerDao.findAll();
    }

    @Override
    @Cacheable(value = "careersByName", key = "#name")
    public Optional<Career> findByName(String name) {
        LOGGER.debug("Getting career by name {}", name);
        return careerDao.findByName(name);
    }

    @Override
    public Page<Career> getAllCareers(String search, PageParams pageParams) {
        LOGGER.debug("Getting all careers with search {}", search);
        if (search == null || search.isEmpty()) {
            return careerDao.getAllCareers(pageParams);
        }
        return careerDao.searchBySubstring(search, pageParams);
    }

    @Override
    @Transactional
    @Caching(put = {
                    @CachePut(value = "careersById", key = "#result.id"),
                    @CachePut(value = "careersByName", key = "#result.name")
    })
    public Career create(String name) {
        return careerDao.create(name);
    }

    @Override
    @Transactional
    @Caching(
            put = { @CachePut(value = "careersById", key = "#id") },
            evict = { @CacheEvict(value = "careersByName", allEntries = true) }
    )
    public Career update(long id, String name) {
        return careerDao.update(id, name);
    }

    @Override
    @Transactional
    @Caching(evict = {
                @CacheEvict(value = "careersById", key = "#id"),
                @CacheEvict(value = "careersByName", allEntries = true)
    })
    public void delete(long id) {
        careerDao.delete(id);
    }

}
