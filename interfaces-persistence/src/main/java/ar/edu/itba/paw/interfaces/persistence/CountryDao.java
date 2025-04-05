package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Country;
import java.util.List;

public interface CountryDao {
    List<Country> findAll();
    Boolean existsByName(String name);
}
