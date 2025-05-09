package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.CursorPage;

import java.util.List;
import java.util.Optional;

public interface CountryDao {
    //Contentido estatico
    List<Country> findAll();
    boolean existsByName(String name);
    Optional<Country> findByName(String name);
}
