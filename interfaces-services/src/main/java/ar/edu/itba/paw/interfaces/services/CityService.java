package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.City;

import java.util.Optional;

public interface CityService {
    Optional<City> findByName(String name);
}
