package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.University;

import java.util.List;
import java.util.Optional;

public interface UniversityDao {
    Optional<University> findByName(String name);
    Optional<University> findByAbbreviation(String abbreviation);
    Optional<University> findByAny(String search);
    List<University> getAllUniversities();
}
