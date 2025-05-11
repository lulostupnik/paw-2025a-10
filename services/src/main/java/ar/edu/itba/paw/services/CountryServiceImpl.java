package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);

    CountryDao countryDao;

    public CountryServiceImpl(final ar.edu.itba.paw.interfaces.persistence.CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @Override
    @Cacheable(value = "countries")
    public List<Country> getAllCountries() {
        LOGGER.debug("Getting all countries");
        return countryDao.findAll();
    }

    @Override
    @Cacheable(value="countriesByName", key="#name")
    public Optional<Country> findByName(String name) {
        return countryDao.findByName(name);
    }

}
