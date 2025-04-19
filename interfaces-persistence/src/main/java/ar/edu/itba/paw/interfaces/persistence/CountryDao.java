package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.CursorPage;

import java.util.List;

public interface CountryDao {
    List<Country> findAll();
    Boolean existsByName(String name);

    CursorPage<Country, Long> findAll(Long cursor, int limit);
    CursorPage<Country, Long> findBySubstring(String substring, Long cursor, int limit);
}
