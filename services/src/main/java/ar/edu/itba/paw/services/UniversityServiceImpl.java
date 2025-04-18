package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.University;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityServiceImpl implements UniversityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversityServiceImpl.class);


    private final UniversityDao universityDao;

    @Autowired
    public UniversityServiceImpl(UniversityDao universityDao) {
        this.universityDao = universityDao;
    }

    @Override
    public Optional<University> findByName(String name) {
        LOGGER.debug("Getting university with name {}", name);
        return universityDao.findByName(name);
    }

    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        LOGGER.debug("Getting university with abbreviation {}", abbreviation);
        return universityDao.findByAbbreviation(abbreviation);
    }

    @Override    
    public Optional<University> findByAny(String queryString){
        LOGGER.debug("Getting university like {}", queryString);
        return universityDao.findByAny(queryString);
    }

    @Override
    public List<University> getAllUniversities() {
        LOGGER.debug("Getting all universities");
        return universityDao.getAllUniversities();
    }

    @Override
    public List<University> searchBySubstring(String substring) {
        return universityDao.searchBySubstring(substring);
    }

    @Override
    public CursorPage<University, Long> getAllUniversitiesAfter(Long cursor, int limit) {
        return universityDao.getAllUniversitiesAfter(cursor, limit);
    }

    @Override
    public CursorPage<University, Long> searchBySubstringAfter(String substring, Long cursor, int limit) {
        return universityDao.searchBySubstringAfter(substring, cursor, limit);
    }

}
