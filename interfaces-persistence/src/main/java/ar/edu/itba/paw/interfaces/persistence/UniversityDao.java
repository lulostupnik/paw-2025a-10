package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.University;
import java.util.List;
import java.util.Optional;

public interface UniversityDao {
    Optional<University> findByName(String name);
    Optional<University> findByAbbreviation(String abbreviation);
    Optional<University> findByAny(String search);
    List<University> getAllUniversities();
    Page<University> searchBySubstring(String substring, int page, int size);
    Optional<University> findById(long id);
    Page<University> getAllUniversities(int page, int size);
    University createUniversity(String name, String abbreviation, String city);
    void updateUniversity(long id, String name, String abbreviation, long cityId);
    void delete(long id);
}
