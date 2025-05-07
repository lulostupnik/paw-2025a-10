package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.*;
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
public class CityServiceImpl implements CityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityDao cityDao;
    private final CountryService countryService;

    @Autowired
    public CityServiceImpl(CityDao cityDao, CountryService countryService) {

        this.cityDao = cityDao;
        this.countryService = countryService;
    }

    @Override
    @Cacheable(value = "citiesByName", key = "#name")
    public Optional<City> findByName(String name) {
        LOGGER.debug("Finding city by name {}", name);
        return cityDao.findByName(name);
    }

    // todo: ¿Cacheable?
    @Override
    public List<City> findAll() {
        LOGGER.debug("Finding all cities");
        return cityDao.findAll();
    }

    @Override
    public Optional<City> findById(Long id) {
        LOGGER.debug("Finding city by id {}", id);
        return cityDao.findBy(id, null, null);
    }

    @Override
    public List<City> findAllByCountry(String country) {
        LOGGER.debug("Finding city by country name {}", country);
        return cityDao.findAllByCountry(country);
    }

    @Override
    public List<City> getAllCities() {
        return cityDao.getAllCities();
    }

    @Override
    public Page<City> getAllCities(String search, PageParams pageParams) {
        LOGGER.debug("Finding all cities with search {}", search);
        if (search == null || search.isEmpty()) {
            return cityDao.getAllCities(pageParams.getPage(), pageParams.getSize());
        }
        return cityDao.searchBySubstring(search, pageParams.getPage(), pageParams.getSize());
    }


    @Transactional
    @Override
    public void updateCity(long id, String name, String country) {
        Country country1 = countryService.findByName(country)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
        cityDao.updateCity(id, name, country1);
    }

    @Transactional
    @Override
    public long createCity(String name, String country) {
        Country country1 = countryService.findByName(country)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
        return cityDao.createCity(name, country1);
    }

    @Transactional
    @Override
    public void delete(long id) {
        cityDao.delete(id);
    }

    @Override
    public Page<City> searchBySubstring(String substring, PageParams pageParams) {
        return cityDao.searchBySubstring(substring, pageParams.getPage(), pageParams.getSize());
    }

}
