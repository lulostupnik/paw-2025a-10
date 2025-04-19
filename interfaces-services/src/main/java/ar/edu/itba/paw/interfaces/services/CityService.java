package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.CursorPage;

import java.util.List;
import java.util.Optional;

public interface CityService {
    Optional<City> findByName(String name);
    List<City> findAll();
    List<City> findAllByCountry(String country);
    List<City> findAllBySubstring(String substring);
    List<City> getAllCities();

    CursorPage<City, Long> findAll(Long cursor, int limit);
    CursorPage<City, Long> findBySubstring(String substring, Long cursor, int limit);
    CursorPage<City, Long> findByCountry(String country, Long cursor, int limit);
    CursorPage<City, Long> getAllCities(Long cursor, int limit);

}
