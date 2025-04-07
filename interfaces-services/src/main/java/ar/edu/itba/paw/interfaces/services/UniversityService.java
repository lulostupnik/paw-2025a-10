package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.University;

import java.util.List;
import java.util.Optional;

public interface UniversityService {
    Optional<University> findByName(String name);
    Optional<University> findByAbbreviation(String abbreviation);
    Optional<University> findByAny(String queryString);
    University registerUniversity(String name, String abbreviation);
    List<University> getAllUniversities();
}
