package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.University;

import java.util.List;
import java.util.Optional;

public interface UniversityService {
    Optional<University> findByName(String name);
    Optional<University> findById(Long id);
    Optional<University> findByAbbreviation(String abbreviation);
    Optional<University> findByAny(String queryString);
    List<University> getAllUniversities();
    List<University> searchBySubstring(String substring);
    void createUniversity(String nameEn,String nameEs, String abbreviation, long cityId);
}
