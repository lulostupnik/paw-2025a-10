package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.CityNotFoundException;
import ar.edu.itba.paw.models.exceptions.CountryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CityServiceImpl implements CityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityDao cityDao;
    private final CountryService countryService;


    @Autowired
    public CityServiceImpl(final CityDao cityDao,final CountryService countryService) {
        this.cityDao = cityDao;
        this.countryService = countryService;
    }

    @Override
    @Cacheable(value = "citiesByName", key = "#name")
    public Optional<City> findCityByName(final String name) {
        LOGGER.debug("Finding city by name {}", name);
        return cityDao.findByName(name);
    }


    @Override
    @Cacheable(value = "citiesById", key = "#id")
    public Optional<City> findCityById(final long id) {
        LOGGER.debug("Finding city by id {}", id);
        return cityDao.findById(id);
    }


    @Override
    public Page<City> searchCities(final String search, final PageParams pageParams) {
        LOGGER.debug("Finding all cities with search {}", search);
        if (search == null || search.isEmpty()) {
            return cityDao.findAll(pageParams);
        }
        return cityDao.search(search, pageParams);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "citiesByName", allEntries = true),
                    @CacheEvict(value = "citiesById", key = "#id"),
                    @CacheEvict(value = "universitiesById", allEntries = true),
                    @CacheEvict(value = "universitiesByName", allEntries = true)
            }
    )
    public void updateCity(final long id,final String name,final String countryName) {
        LOGGER.debug("Updating city with id {}, name {}, country {}", id, name, countryName);
        Country country = countryService.findCountryByName(countryName)
                .orElseThrow(() -> {
                    LOGGER.error("Country {} not found", countryName);
                    return new CountryNotFoundException("Country not found for name", countryName);});
        City city = cityDao.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("City with id {} not found", id);
                    return new CityNotFoundException(id);});
        city.setName(name);
        city.setCountry(country);
        LOGGER.info("City with id {} updated successfully", id);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "universitiesById", allEntries = true),
                    @CacheEvict(value = "universitiesByName", allEntries = true)
            }
    )
    public City createCity(final String cityName,final String countryName) {
        LOGGER.debug("Creating city with name {} and country {}", cityName, countryName);
        Country country = countryService.findCountryByName(countryName)
                .orElseThrow(() -> {
                    LOGGER.error("Country {} not found", countryName);
                    return new CountryNotFoundException("Country not found for name", countryName);});
        City city = cityDao.create(cityName, country);
        LOGGER.info("City with name {} and country {} created successfully", cityName, countryName);
        return city;
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "citiesById", key = "#id"),
                    @CacheEvict(value = "citiesByName", allEntries = true),
                    @CacheEvict(value = "universitiesById", allEntries = true),
                    @CacheEvict(value = "universitiesByName", allEntries = true)
            }
    )
    public void deleteCity(final long id) {
       Optional<City> maybeCity = cityDao.findById(id);
        if (maybeCity.isEmpty()) {
            LOGGER.error("City with id {} not found", id);
            return;
        }
        maybeCity.get().setDeleted(true);
        LOGGER.info("City with id {} deleted successfully", id);
    }

}
