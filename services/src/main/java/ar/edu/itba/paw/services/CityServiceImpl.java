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


    @Override
    @Cacheable(value = "citiesById", key = "#id")
    public Optional<City> findById(Long id) {
        LOGGER.debug("Finding city by id {}", id);
        return cityDao.findBy(id, null, null);
    }


    @Override
    public Page<City> getAllCities(String search, PageParams pageParams) {
        LOGGER.debug("Finding all cities with search {}", search);
        if (search == null || search.isEmpty()) {
            return cityDao.getAllCities(pageParams);
        }
        return cityDao.searchBySubstring(search, pageParams);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "citiesByName", allEntries = true),
                    @CacheEvict(value = "citiesById", key = "#id"),
                    @CacheEvict(value = "cities", allEntries = true),
                    @CacheEvict(value = "universities", allEntries = true),
                    @CacheEvict(value = "universitiesById", allEntries = true),
                    @CacheEvict(value = "universitiesByName", allEntries = true)
            }
    )
    public void updateCity(long id, String name, String countryName) {
        Country country = countryService.findByName(countryName)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
        cityDao.updateCity(id, name, country);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value= "cities", allEntries = true),
                    @CacheEvict(value = "universities", allEntries = true),
                    @CacheEvict(value = "universitiesById", allEntries = true),
                    @CacheEvict(value = "universitiesByName", allEntries = true)
            }
    )
    public long createCity(String cityName, String countryName) {
        Country country = countryService.findByName(countryName)
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));
        return cityDao.createCity(cityName, country);
    }

    @Override
    public String getCitiesJson(String search, PageParams pageParams) {
        LOGGER.debug("Finding all cities with search {}", search);
        List<City> cities;
        if (search == null || search.isEmpty()) {
            cities = cityDao.getAllCities(pageParams).getContent();
            return listToJson(cities);

        }
        cities = cityDao.searchBySubstring(search, pageParams).getContent();
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
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "citiesById", key = "#id"),
                    @CacheEvict(value = "citiesByName", allEntries = true),
                    @CacheEvict(value = "cities", allEntries = true),
                    @CacheEvict(value = "universities", allEntries = true),
                    @CacheEvict(value = "universitiesById", allEntries = true),
                    @CacheEvict(value = "universitiesByName", allEntries = true)
            }
    )
    public void delete(long id) {
        cityDao.delete(id);
    }

    @Override
    public Page<City> searchBySubstring(String substring, PageParams pageParams) {
        return cityDao.searchBySubstring(substring, pageParams);
    }

}
