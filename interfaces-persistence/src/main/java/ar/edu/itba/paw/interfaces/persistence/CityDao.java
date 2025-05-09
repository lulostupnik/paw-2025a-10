package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface CityDao {
    Optional<City> findBy(Long id, String name, String country);

    List<City> findAllByCountry(String country);
    Optional<City> findByName(String name); // method to find a city by its name
    List<City> getAllCities();
    // add method that finds cities with a "similar" name -> using Levenshtein?
    Page<City> searchBySubstring(String substring, PageParams pageParams);
    Page<City> getAllCities(PageParams pageParams);
    void updateCity(long id, String name, Country country); // method to update a city by its id
    long createCity(String nameEn, Country country);

    void delete(long id);
}
