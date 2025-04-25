package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.CursorPage;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries();
    Boolean existsByName(String name);

}
