package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Country;
import java.util.List;
import java.util.Optional;

public interface CountryDao {
    List<Country> findAll();
    Optional<Country> findByName(String name);
}
