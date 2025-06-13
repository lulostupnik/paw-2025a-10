package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import java.util.Optional;

public interface UniversityDao {
    University create(String name, String abbreviation, City city);
    void delete(long id);
    Optional<University> findByName(String name);
    Optional<University> findById(long id);
    Page<University> search(String searchTerm, PageParams pageParams);
    Page<University> findAll(PageParams pageParams);

}
