package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "universitiesByName", key = "#name")
    public Optional<University> findByName(String name) {
        LOGGER.debug("Getting university with name {}", name);
        return universityDao.findByName(name);
    }

    @Override
    @Cacheable(value = "universitiesById", key = "#id")
    public Optional<University> findById(long id) {
        return universityDao.findById(id);
    }




    @Override
    public Page<University> getAllUniversities(String search, PageParams pageParams) {
        LOGGER.debug("Getting all universities with search {}", search);
        if (search == null || search.isEmpty()) {
            return universityDao.findAll(pageParams);
        }
        return universityDao.search(search, pageParams);
    }
    @Override
    public String getUniversitiesJSON(String search, PageParams pageParams){
        LOGGER.debug("Getting all universities with search {}", search);
        List<University> universities;
        if (search == null || search.isEmpty()) {
            universities = universityDao.findAll(pageParams).getContent();
            return UniversitiesToJson(universities);
        }
        universities = universityDao.search(search,pageParams).getContent();

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
    @Caching(
            put = {
                @CachePut(value = "universitiesById", key = "#result.id"),
                @CachePut(value = "universitiesByName", key = "#result.name")
            },
            evict = {
                @CacheEvict(value = "universities", allEntries = true)
            }
    )
    public University createUniversity(String name, String abbreviation, String city) {
        return universityDao.create(name, abbreviation, city);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "universities", allEntries = true),
            @CacheEvict(value = "universitiesById", key = "#id"),
            @CacheEvict(value = "universitiesByName", allEntries = true)
    })
    public void updateUniversity(long id, String name, String abbreviation, String cityName) {
        universityDao.update(id, name, abbreviation, cityName);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "universities", allEntries = true),
                    @CacheEvict(value = "universitiesById", key = "#id"),
                    @CacheEvict(value = "universitiesByName", allEntries = true)
            }
    )
    public void delete(long id) {
        universityDao.delete(id);
    }


}

//
//    @Override
//    public Page<University> searchUniversities(String search, PageParams pageParams) {
//        return universityDao.searchBySubstring(search, pageParams);
//    }

//
//    @Override
//    public Optional<University> findByAbbreviation(String abbreviation) {
//        LOGGER.debug("Getting university with abbreviation {}", abbreviation);
//        return universityDao.findByAbbreviation(abbreviation);
//    }
//
//    @Override
//    public Optional<University> findByAny(String queryString){
//        LOGGER.debug("Getting university like {}", queryString);
//        return universityDao.findByAny(queryString);
//    }