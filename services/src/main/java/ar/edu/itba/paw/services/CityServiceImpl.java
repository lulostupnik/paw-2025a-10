package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.*;
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
    public Optional<City> findByName(final String name) {
        LOGGER.debug("Finding city by name {}", name);
        return cityDao.findByName(name);
    }


    @Override
    @Cacheable(value = "citiesById", key = "#id")
    public Optional<City> findById(final long id) {
        LOGGER.debug("Finding city by id {}", id);
        return cityDao.findById(id);
    }


    @Override
    public Page<City> getAllCities(final String search,final PageParams pageParams) {
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
        Country country = countryService.findByName(countryName)
                .orElseThrow(() -> {
                    LOGGER.error("Country {} not found", countryName);
                    return new IllegalArgumentException("Country not found");});
        cityDao.update(id, name, country);
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
        Country country = countryService.findByName(countryName)
                .orElseThrow(() -> {
                    LOGGER.error("Country {} not found", countryName);
                    return new IllegalArgumentException("Country not found");});
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
    public void delete(final long id) {
        LOGGER.debug("Deleting city with id {}", id);
        cityDao.delete(id);
        LOGGER.info("City with id {} deleted successfully", id);
    }

    @Override
    public Page<City> searchBySubstring(final String substring,final PageParams pageParams) {
        LOGGER.debug("Searching cities with substring {}", substring);
        return cityDao.search(substring, pageParams);
    }

}
