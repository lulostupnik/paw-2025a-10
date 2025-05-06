package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.University;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityServiceImpl implements UniversityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversityServiceImpl.class);


    private final UniversityDao universityDao;
    private final CityService cityService;

    @Autowired
    public UniversityServiceImpl(UniversityDao universityDao, CityService cityService) {

        this.universityDao = universityDao;
        this.cityService = cityService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "universitiesByName", key = "#name")
    @Override
    public Optional<University> findByName(String name) {
        LOGGER.debug("Getting university with name {}", name);
        return universityDao.findByName(name);
    }

    @Override
    public Optional<University> findById(Long id) {
        return universityDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "universitiesByAbbreviation", key = "#abbreviation")
    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        LOGGER.debug("Getting university with abbreviation {}", abbreviation);
        return universityDao.findByAbbreviation(abbreviation);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "universitiesByAny", key = "#queryString")
    @Override    
    public Optional<University> findByAny(String queryString){
        LOGGER.debug("Getting university like {}", queryString);
        return universityDao.findByAny(queryString);
    }

    // FIXME: ¿debería ser @Cacheable?
    @Transactional(readOnly = true)
    @Cacheable(value = "universities")
    @Override
    public List<University> getAllUniversities() {
        LOGGER.debug("Getting all universities");
        return universityDao.getAllUniversities();
    }


    // TODO: ¿Hace falta el if?
    @Override
    public Page<University> getAllUniversities(String search, int page, int size) {
        LOGGER.debug("Getting all universities with search {}", search);
        if (search == null || search.isEmpty()) {
            return universityDao.getAllUniversities(page, size);
        }
        return universityDao.searchBySubstring(search, page, size);
    }

    @Override
    public University createUniversity(String name, String abbreviation, String city) {
        return universityDao.createUniversity(name, abbreviation, city);
    }

    @Override
    public void updateUniversity(long id, String name, String abbreviation, String cityName) {
//        City city = cityService.findByName(cityName).orElseThrow(() -> new IllegalArgumentException("City not found"));
//        universityDao.updateUniversity(id, name, abbreviation, city.getId());
        universityDao.updateUniversity(id, name, abbreviation, cityName);
    }

    @Override
    public Page<University> searchUniversities(String search, int page, int size) {
        return universityDao.searchBySubstring(search, page, size);
    }

    @Override
    public void delete(long id) {
        universityDao.delete(id);
    }


}
