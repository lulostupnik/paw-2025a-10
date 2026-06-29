package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;
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
    public University createUniversity(final String name, final String abbreviation, final long cityId) {
        LOGGER.debug("Creating university with name {}, abbreviation {}, city {}", name, abbreviation, cityId);
        City city = cityService.findCityById(cityId).orElseThrow(() -> {
            LOGGER.error("City not found with id: {}", cityId);
            return new InvalidReferenceException("City", String.valueOf(cityId));
        });
        University university = universityDao.create(name, abbreviation, city);
        LOGGER.info("University created successfully with name: {}, abbreviation: {}, in city: {}", name, abbreviation, cityId);
        return university;
    }

    @Override
    @Transactional
    public University updateUniversity(final long id, final String name, final String abbreviation, final long cityId) {
        LOGGER.debug("Updating university with id {}, name {}, abbreviation {}, city {}", id, name, abbreviation, cityId);
        City city = cityService.findCityById(cityId).orElseThrow(() -> new InvalidReferenceException("City", String.valueOf(cityId)));
        University university = universityDao.findById(id).orElseThrow(() -> new UniversityNotFoundException(id));
        university.setName(name);
        university.setAbbreviation(abbreviation);
        university.setCity(city);

        LOGGER.info("University updated successfully with id: {}, name: {}, abbreviation: {}, city: {}", id, name, abbreviation, cityId);
        return university;
    }

    @Override
    @Transactional
    public University patchUniversity(final long id, final String name, final String abbreviation, final Long cityId) {
        LOGGER.debug("Patching university with id {}", id);
        University university = universityDao.findById(id).orElseThrow(() -> new UniversityNotFoundException(id));

        if (name != null) {
            university.setName(name);
        }
        if (abbreviation != null) {
            university.setAbbreviation(abbreviation);
        }
        if (cityId != null) {
            City city = cityService.findCityById(cityId).orElseThrow(() -> new InvalidReferenceException("City", String.valueOf(cityId)));
            university.setCity(city);
        }

        LOGGER.info("University patched successfully with id: {}", id);
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
