package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;

import java.util.Optional;

public interface UniversityDao {
    University create(String name, String abbreviation, String cityName);
    void update(long id, String newName, String newAbbreviation, String newCityName);
    void delete(long id);
    Optional<University> findByName(String name);
    Optional<University> findById(long id);
    Page<University> search(String substring, PageParams pageParams);
    Page<University> findAll(PageParams pageParams);

    // No se están usando, pero tal vez se podrían/deberían usar
    void update(long id, String newName, String newAbbreviation, long newCityId);
}
