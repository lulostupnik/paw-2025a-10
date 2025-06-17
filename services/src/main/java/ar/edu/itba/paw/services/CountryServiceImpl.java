package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final CountryDao countryDao;

    @Autowired
    public CountryServiceImpl(final CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @Override
    public List<Country> findCountries() {
        return countryDao.findAll();
    }

    @Override
    public Optional<Country> findCountryByName(final String name) {
        LOGGER.debug("Getting country {}", name);
        return countryDao.findByName(name);
    }

}
