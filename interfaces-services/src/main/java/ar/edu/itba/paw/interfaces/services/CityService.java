package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;

import java.util.List;
import java.util.Optional;

public interface CityService {
    Optional<City> findByName(String name);
    List<City> findAll();
    List<City> findAllByCountry(String country);
    List<City> findAllBySubstring(String substring);
    List<City> getAllCities();
    Page<City> getAllCities(String search, int page, int pageSize);
    void updateCity(long id, String name, String country);
    void createCity(String name, String country);

}
