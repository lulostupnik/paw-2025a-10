package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.util.Optional;

public interface CareerDao {
    Optional<Career> findById(long id);
    Optional<Career> findByName(String name);
    Page<Career> findAll(PageParams pageParams);
    Page<Career> search(String substring, PageParams pageParams);
    Career create(String name);
    void delete(long id);
}
