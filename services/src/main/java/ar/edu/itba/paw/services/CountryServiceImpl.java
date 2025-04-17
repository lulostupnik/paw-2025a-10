package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final ar.edu.itba.paw.interfaces.persistence.CountryDao countryDao;

    public CountryServiceImpl(final ar.edu.itba.paw.interfaces.persistence.CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @Override
    public List<Country> getAllCountries() {
        LOGGER.debug("Getting all countries");
        return countryDao.findAll();
    }

    @Override
    public Boolean existsByName(String name) {
        LOGGER.debug("Getting country {}", name);
        return countryDao.existsByName(name);
    }
}
