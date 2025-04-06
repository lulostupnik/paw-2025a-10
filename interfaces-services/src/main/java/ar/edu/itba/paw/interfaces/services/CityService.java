package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;

import java.util.List;
import java.util.Optional;

public interface CityService {
    Optional<City> findByName(String name);
    List<City> findAll();
    List<City> findAllByCountry(String country);
    List<City> findAllBySubstring(String substring);
    List<City> getAllCities();
}
