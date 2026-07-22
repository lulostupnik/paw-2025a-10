package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import java.util.Optional;

public interface UniversityService {
    Optional<University> findByName(String name);
    Optional<University> findById(long id);
    Page<University> findUniversities(String search, PageParams pageParams);
    University createUniversity(String name, String abbreviation, long cityId);
    University patchUniversity(long id, String name, String abbreviation, Long cityId);
    void deleteUniversity(long id);
}
