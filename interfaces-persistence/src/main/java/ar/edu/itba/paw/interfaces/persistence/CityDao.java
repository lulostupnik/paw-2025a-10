package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;

import java.util.List;
import java.util.Optional;

public interface CityDao {
    Optional<City> findBy(Long id, String name, String country);
    List<City> findAll();
    List<City> findAllByCountry(String country);
    Optional<City> findByName(String name); // method to find a city by its name
    List<City> getAllCities();
    // add method that finds cities with a "similar" name -> using Levenshtein?
    Page<City> searchBySubstring(String substring, int page, int size);
    Page<City> getAllCities(int page, int pageSize);
    void updateCity(long id, String name, Country country); // method to update a city by its id
    long createCity(String nameEn, Country country);

    void delete(long id);
}
