package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.Optional;

public interface CityDao {
    Optional<City> findBy(Long id, String name, String country);
    Optional<City> findByName(String name); // method to find a city by its name
    Page<City> searchBySubstring(String substring, PageParams pageParams);
    Page<City> getAllCities(PageParams pageParams);
    void updateCity(long id, String name, Country country); // method to update a city by its id
    long createCity(String nameEn, Country country);
    void delete(long id);
}
