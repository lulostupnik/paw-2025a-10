package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.models.Career;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareerServiceImpl implements CareerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CareerServiceImpl.class);

    private final CareerDao careerDao;

    @Autowired
    public CareerServiceImpl(CareerDao careerDao) {
        this.careerDao = careerDao;
    }

    @Override
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
    public Optional<Career> findByName(String name) {
        LOGGER.debug("Getting career by name {}", name);
        return careerDao.findByName(name);
    }

}
