package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.exceptions.CityNotFoundException;
import ar.edu.itba.paw.models.exceptions.UniversityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UniversityServiceImpl implements UniversityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversityServiceImpl.class);

    private final UniversityDao universityDao;
    private final CityService cityService;

    @Autowired
    public UniversityServiceImpl(final UniversityDao universityDao, final CityService cityService) {
        this.universityDao = universityDao;
        this.cityService = cityService;
    }

    @Override
    public Optional<University> findByName(final String name) {
        LOGGER.debug("Getting university with name {}", name);
        return universityDao.findByName(name);
    }

    @Override
    public Optional<University> findById(final long id) {
        LOGGER.debug("Getting university with id {}", id);
        return universityDao.findById(id);
    }


    @Override
    public Page<University> findUniversities(final String search, final PageParams pageParams) {
        LOGGER.debug("Getting all universities with search {} and pageParams {}", search, pageParams);
        if (search == null || search.isEmpty()) {
            return universityDao.findAll(pageParams);
        }
        return universityDao.search(search, pageParams);
    }

    @Override
    @Transactional
    public University createUniversity(final String name, final String abbreviation, final String cityName) {
        LOGGER.debug("Creating university with name {}, abbreviation {}, city {}", name, abbreviation, cityName);
        City city = cityService.findCityByName(cityName).orElseThrow(() -> {
            LOGGER.error("City not found with name: {}", cityName);
            return new CityNotFoundException(cityName);
        });
        University university = universityDao.create(name, abbreviation, city);
        LOGGER.info("University created successfully with name: {}, abbreviation: {}, in city: {}", name, abbreviation, cityName);
        return university;
    }

    @Override
    @Transactional
    public University updateUniversity(final long id, final String name, final String abbreviation, final String cityName) {
        LOGGER.debug("Updating university with id {}, name {}, abbreviation {}, city {}", id, name, abbreviation, cityName);
        City city = cityService.findCityByName(cityName).orElseThrow(() -> {
            LOGGER.error("City not found with name: {}", cityName);
            return new CityNotFoundException(cityName);
        });
        University university = universityDao.findById(id).orElseThrow(() -> {
            LOGGER.error("University with id {} not found", id);
            return new UniversityNotFoundException(id);
        });
        university.setName(name);
        university.setAbbreviation(abbreviation);
        university.setCity(city);

        LOGGER.info("University updated successfully with id: {}, name: {}, abbreviation: {}, city: {}", id, name, abbreviation, cityName);
        return university;
    }

    @Override
    @Transactional
    public void deleteUniversity(final long id) {
        Optional<University> maybeUniversity = universityDao.findById(id);
        if (maybeUniversity.isEmpty()) {
            LOGGER.info("University with id {} not found", id);
            return;
        }
        maybeUniversity.get().setDeleted(true);
        LOGGER.info("University deleted successfully with id: {}", id);
    }


}
