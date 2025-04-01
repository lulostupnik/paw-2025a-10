package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.City;

import java.util.Optional;

public interface CityDao {
    Optional<City> findBy(long id, String name, String country);
}
