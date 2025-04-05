package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CityServiceImpl implements CityService {

    private final CityDao cityDao;

    @Autowired
    public CityServiceImpl(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    @Override
    public Optional<City> findByName(String name) {
        return cityDao.findBy(null, name, null);
    }
}
