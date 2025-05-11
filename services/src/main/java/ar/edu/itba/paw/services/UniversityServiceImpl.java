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

    @Autowired
    public UniversityServiceImpl(final UniversityDao universityDao) {
        this.universityDao = universityDao;
    }

    @Override
    @Cacheable(value = "universitiesByName", key = "#name")
    public Optional<University> findByName(final String name) {
        LOGGER.debug("Getting university with name {}", name);
        return universityDao.findByName(name);
    }

    @Override
    @Cacheable(value = "universitiesById", key = "#id")
    public Optional<University> findById(final long id) {
        return universityDao.findById(id);
    }




    @Override
    public Page<University> getAllUniversities(final String search, final PageParams pageParams) {
        LOGGER.debug("Getting all universities with search {}", search);
        if (search == null || search.isEmpty()) {
            return universityDao.findAll(pageParams);
        }
        return universityDao.search(search, pageParams);
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
    public University createUniversity(final String name, final String abbreviation,final  String city) {
        return universityDao.create(name, abbreviation, city);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "universities", allEntries = true),
            @CacheEvict(value = "universitiesById", key = "#id"),
            @CacheEvict(value = "universitiesByName", allEntries = true)
    })
    public void updateUniversity(final long id, final String name, final String abbreviation, final String cityName) {
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
    public void delete(final long id) {
        universityDao.delete(id);
    }


}
