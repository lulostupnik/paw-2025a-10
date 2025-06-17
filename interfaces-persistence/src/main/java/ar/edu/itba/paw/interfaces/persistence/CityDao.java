package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.util.Optional;

public interface CityDao {
    Optional<City> findById(long id);
    Optional<City> findByName(String name);
    Page<City> search(String substring, PageParams pageParams);
    Page<City> findAll(PageParams pageParams);
    City create(String nameEn, Country country);
}
