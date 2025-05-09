package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.util.List;
import java.util.Optional;

public interface CityService {
    Optional<City> findByName(String name);
    Optional<City> findById(Long id);
    List<City> findAllByCountry(String country);
    Page<City> getAllCities(String search, PageParams pageParams);
    Page<City> searchBySubstring(String substring, PageParams pageParams);
    void updateCity(long id, String name, String country);
    String getCitiesJson(String search, PageParams pageParams);
    long createCity(String name, String country);

    void delete(long id);
}
