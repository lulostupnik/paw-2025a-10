package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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

    @Override
    public List<City> findAll() {
        return cityDao.findAll();
    }

    @Override
    public List<City> findAllByCountry(String country) {
        return cityDao.findAllByCountry(country);
    }

    @Override
    public List<City> findAllBySubstring(String substring) {
        return cityDao.findAllBySubstring(substring);
    }

}
