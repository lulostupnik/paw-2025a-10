package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.util.Optional;

public interface CityService {
    Optional<City> findCityByName(String name);
    Optional<City> findCityById(long id);
    Page<City> searchCities(String search, PageParams pageParams);
    City updateCity(long id, String name, long countryId);
    City patchCity(long id, String name, Long countryId);
    City createCity(String name, long countryId);

    void deleteCity(long id);
}
