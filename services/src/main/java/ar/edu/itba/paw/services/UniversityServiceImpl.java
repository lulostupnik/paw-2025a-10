package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
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
@Transactional(readOnly = true)
public class UniversityServiceImpl implements UniversityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversityServiceImpl.class);

    private final UniversityDao universityDao;
    private static final int DEFAULT_PAGE_SIZE = 30;

    @Autowired
    public UniversityServiceImpl(UniversityDao universityDao) {
        this.universityDao = universityDao;
    }

    @Override
    // @Cacheable(value = "universitiesByName", key = "#name")
    public Optional<University> findByName(String name) {
        LOGGER.debug("Getting university with name {}", name);
        return universityDao.findByName(name);
    }

    @Override
    public Optional<University> findById(Long id) {
        return universityDao.findById(id);
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

    // @Cacheable(value = "universities")
    @Override
    public List<University> getAllUniversities() {
        LOGGER.debug("Getting all universities");
        return universityDao.getAllUniversities();
    }


    @Override
    public Page<University> getAllUniversities(String search, PageParams pageParams) {
        LOGGER.debug("Getting all universities with search {}", search);
        if (search == null || search.isEmpty()) {
            return universityDao.getAllUniversities(pageParams);
        }
        return universityDao.searchBySubstring(search, pageParams);
    }
    @Override
    public String getUniversitiesJSON(String search, PageParams pageParams){
        LOGGER.debug("Getting all universities with search {}", search);
        List<University> universities;
        if (search == null || search.isEmpty()) {
            universities = universityDao.getAllUniversities(pageParams).getContent();
            return UniversitiesToJson(universities);
        }
        universities = universityDao.searchBySubstring(search,pageParams).getContent();

        return UniversitiesToJson(universities);
    }

    private String UniversitiesToJson(List<University> universities) {
        StringBuilder json = new StringBuilder("[");
        for (University university : universities) {
            json.append(university.toJSON()).append(",");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // Remove last comma
        }
        json.append("]");
        LOGGER.debug("JSON universities: {}", json);
        return json.toString();
    }

    @Override
    @Transactional
    public University createUniversity(String name, String abbreviation, String city) {
        return universityDao.createUniversity(name, abbreviation, city);
    }

    @Override
    @Transactional
    public void updateUniversity(long id, String name, String abbreviation, String cityName) {
        universityDao.updateUniversity(id, name, abbreviation, cityName);
    }

    @Override
    public Page<University> searchUniversities(String search, PageParams pageParams) {
        return universityDao.searchBySubstring(search, pageParams);
    }

    @Override
    @Transactional
    public void delete(long id) {
        universityDao.delete(id);
    }


}
