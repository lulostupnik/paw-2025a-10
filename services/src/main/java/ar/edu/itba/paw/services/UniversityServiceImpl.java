package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.models.University;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UniversityServiceImpl implements UniversityService {
    // @Autowired
    // private final UniversityDao universityDao;

    /*
    public UniversityServiceImpl(UniversityDao universityDao) {
        this.universityDao = universityDao;
    }
    */

    public Optional<University> findByName(String name) {
        // return universityDao.findByName(name);
        University university = new University(name);
        return Optional.of(university);
    }
}
