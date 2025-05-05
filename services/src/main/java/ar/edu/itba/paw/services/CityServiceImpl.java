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

    private static final int DEFAULT_PAGE_SIZE = 30;

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
    public Page<City> getAllCities(String search, int page, int pageSize) {
        LOGGER.debug("Finding all cities with search {}", search);
        if (search == null || search.isEmpty()) {
            return cityDao.getAllCities(page, pageSize);
        }
        return cityDao.searchBySubstring(search,page, pageSize);
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
    public void createCity(String name, String country) {
        Country country1 = countryService.findByName(country)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
        cityDao.createCity(name, country1);
    }

    @Override
    public String getCitiesJson(String search) {
        LOGGER.debug("Finding all cities with search {}", search);
        List<City> cities;
        if (search == null || search.isEmpty()) {
            cities = cityDao.getAllCities(1,DEFAULT_PAGE_SIZE).getContent();
            return listToJson(cities);

        }
        cities = cityDao.searchBySubstring(search, 1, DEFAULT_PAGE_SIZE).getContent();
        return listToJson(cities);


    }

    private String listToJson(List<City> cities) {
        StringBuilder json = new StringBuilder("[");
        for (City city : cities) {
            json.append(city.toJSON()).append(",");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // Remove last comma
        }
        json.append("]");
        LOGGER.debug("JSON universities: {}", json);
        return json.toString();
    }

    @Override
    public void delete(long id) {
        cityDao.delete(id);
    }

    @Override
    public Page<City> searchBySubstring(String substring, int page, int size) {
        return cityDao.searchBySubstring(substring, page, size);
    }

}
