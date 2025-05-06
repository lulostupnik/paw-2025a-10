package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;

import java.util.List;
import java.util.Optional;

public interface CityService {
    Optional<City> findByName(String name);
    List<City> findAll();
    Optional<City> findById(Long id);
    List<City> findAllByCountry(String country);
    List<City> getAllCities();
    Page<City> getAllCities(String search, int page, int pageSize);
    Page<City> searchBySubstring(String substring, int page, int size);
    void updateCity(long id, String name, String country);
    long createCity(String name, String country);

    void delete(long id);
}
