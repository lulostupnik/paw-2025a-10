package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;

import java.util.Optional;

public interface UniversityService {
    Optional<University> findByName(String name);
    Optional<University> findById(Long id);
    Optional<University> findByAbbreviation(String abbreviation);
    Optional<University> findByAny(String queryString);
    Page<University> getAllUniversities(String search, PageParams pageParams);
    String getUniversitiesJSON(String search, PageParams pageParams);
    University createUniversity(String name, String abbreviation, String city);
    void updateUniversity(long id, String name, String abbreviation, String city);
    Page<University> searchUniversities(String search, PageParams pageParams);

    void delete(long id);
}
