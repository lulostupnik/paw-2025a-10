package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;

import java.util.Optional;

public interface UniversityDao {
    University createUniversity(String name, String abbreviation, String city);

    void updateUniversity(long id, String name, String abbreviation, long cityId);
    void updateUniversity(long id, String name, String abbreviation, String city);

    void delete(long id);

    Optional<University> findByName(String name);
    Optional<University> findByAbbreviation(String abbreviation);
    Optional<University> findByAny(String search);
    Optional<University> findById(long id);

    Page<University> searchBySubstring(String substring, PageParams pageParams);
    Page<University> getAllUniversities(PageParams pageParams);

}
