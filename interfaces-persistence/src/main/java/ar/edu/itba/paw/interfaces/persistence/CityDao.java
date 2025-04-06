package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.City;

import java.util.List;
import java.util.Optional;

public interface CityDao {
    Optional<City> findBy(Long id, String name, String country);
    List<City> findAll();
    List<City> findAllByCountry(String country);
    List<City> findAllBySubstring(String substring); // method to find all the cities which have substring in their name
    Optional<City> findByName(String name); // method to find a city by its name
    // add method that finds cities with a "similar" name -> using Levensthein?
}
