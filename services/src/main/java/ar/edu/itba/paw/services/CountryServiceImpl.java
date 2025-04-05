package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;

import java.util.List;

public class CountryServiceImpl implements CountryService {

    private final ar.edu.itba.paw.interfaces.persistence.CountryDao countryDao;

    public CountryServiceImpl(final ar.edu.itba.paw.interfaces.persistence.CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @Override
    public List<Country> getAllCountries() {
        return countryDao.findAll();
    }

    @Override
    public Boolean existsByName(String name) {
        return countryDao.existsByName(name);
    }
}
