package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;

import ar.edu.itba.paw.models.CursorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityServiceImpl implements CityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityDao cityDao;

    @Autowired
    public CityServiceImpl(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    @Override
    public Optional<City> findByName(String name) {
        LOGGER.debug("Finding city by name {}", name);
        return cityDao.findByName(name);
    }

    @Override
    public List<City> findAll() {
        LOGGER.debug("Finding all cities");
        return cityDao.findAll();
    }

    @Override
    public List<City> findAllByCountry(String country) {
        LOGGER.debug("Finding city by country name {}", country);
        return cityDao.findAllByCountry(country);
    }

    @Override
    public List<City> findAllBySubstring(String substring) {
        LOGGER.debug("Finding city by substring {}", substring);
        return cityDao.findAllBySubstring(substring);
    }

    @Override
    public List<City> getAllCities() {
        return cityDao.getAllCities();
    }

    @Override
    public CursorPage<City, Long> findAll(Long cursor, int limit) {
        return cityDao.findAll(cursor, limit);
    }

    @Override
    public CursorPage<City, Long> findBySubstring(String substring, Long cursor, int limit) {
        return cityDao.findAllBySubstring(substring, cursor, limit);
    }

    @Override
    public CursorPage<City, Long> findByCountry(String country, Long cursor, int limit) {
        return cityDao.findAllByCountry(country, cursor, limit);
    }

    @Override
    public CursorPage<City, Long> getAllCities(Long cursor, int limit) {
        return findAll(cursor, limit);
    }

}
