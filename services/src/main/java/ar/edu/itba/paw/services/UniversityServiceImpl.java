package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.University;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UniversityServiceImpl implements UniversityService {

    private final UniversityDao universityDao;

    @Autowired
    public UniversityServiceImpl(UniversityDao universityDao) {
        this.universityDao = universityDao;
    }

    @Override
    public Optional<University> findByName(String name) {
        return universityDao.findByName(name);
    }

    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        return universityDao.findByAbbreviation(abbreviation);
    }

    @Override    
    public Optional<University> findByAny(String queryString){
        return universityDao.findByAny(queryString);
    }

    @Override
    public University registerUniversity(String name, String abbreviation) {
        return universityDao.createUniversity(name, abbreviation);
    }

}
