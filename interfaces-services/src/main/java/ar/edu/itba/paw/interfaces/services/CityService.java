package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.util.Optional;

public interface CityService {
    Optional<City> findByName(String name);
    Optional<City> findById(long id);
    Page<City> getAllCities(String search, PageParams pageParams);
    Page<City> searchBySubstring(String substring, PageParams pageParams);
    void updateCity(long id, String name, String country);
    long createCity(String name, String country);

    void delete(long id);
}
