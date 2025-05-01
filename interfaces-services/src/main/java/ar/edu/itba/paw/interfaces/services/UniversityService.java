package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.University;

import java.util.List;
import java.util.Optional;

public interface UniversityService {
    Optional<University> findByName(String name);
    Optional<University> findById(Long id);
    Optional<University> findByAbbreviation(String abbreviation);
    Optional<University> findByAny(String queryString);
    List<University> getAllUniversities();
    Page<University> getAllUniversities(String search, int page, int size);
    University createUniversity(String name, String abbreviation, String city);
    void updateUniversity(long id, String name, String abbreviation, long cityId);
    Page<University> searchUniversities(String search, int page, int size);
}
