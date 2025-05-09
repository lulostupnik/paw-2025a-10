package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.util.Optional;

public interface CareerDao {
    Optional<Career> findById(long id);
    Optional<Career> findByName(String name);
    Page<Career> getAllCareers(PageParams pageParams);
    Page<Career> searchBySubstring(String substring, PageParams pageParams);
    Career create(String name);
    Career update(long id, String name);
    void delete(long id);
}
