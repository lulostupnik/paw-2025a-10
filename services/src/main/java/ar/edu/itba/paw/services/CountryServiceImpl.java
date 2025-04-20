package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.CursorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);

    CountryDao countryDao;

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

    @Override
    public CursorPage<Country, Long> getAllCountries(Long cursor, int limit) {
        return countryDao.findAll(cursor, limit);
    }

    @Override
    public CursorPage<Country, Long> getCountriesBySubstring(String substring, Long cursor, int limit) {
        return countryDao.findBySubstring(substring, cursor, limit);
    }
}
