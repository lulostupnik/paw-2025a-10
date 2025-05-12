package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.util.Optional;

public interface CityService {
    Optional<City> findCityByName(String name);
    Optional<City> findCityById(long id);
    Page<City> searchCities(String search, PageParams pageParams);
    void updateCity(long id, String name, String country);
    City createCity(String name, String country);

    void deleteCity(long id);
}
