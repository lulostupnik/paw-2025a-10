package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.CursorPage;
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
    @Cacheable(value = "careers", unless = "#result.size() > 100") // ¿tiene sentido?
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
    public Page<Career> getAllCareers(int page, int pageSize) {
        return careerDao.getAllCareers(page, pageSize);
    }

    @Transactional(readOnly = false)
    @Override
    public Career create(String name) {
        return careerDao.create(name);
    }

    @Transactional(readOnly = false)
    @Override
    public Career update(String oldName, String newName) {
        return careerDao.update(oldName, newName);
    }

}
