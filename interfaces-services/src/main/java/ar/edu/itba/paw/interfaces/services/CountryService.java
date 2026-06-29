package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Country;
import java.util.List;
import java.util.Optional;

public interface CountryService {
    List<Country> findCountries();
    Optional<Country> findCountryById(long id);
    Optional<Country> findCountryByName(String name);
}
