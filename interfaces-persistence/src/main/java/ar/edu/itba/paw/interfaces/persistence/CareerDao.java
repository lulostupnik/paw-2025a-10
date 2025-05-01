package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;

import java.util.List;
import java.util.Optional;

public interface CareerDao {
    Optional<Career> findById(long id);
    List<Career> findAll();
    Optional<Career> findByName(String name);
    Page<Career> getAllCareers(int page, int pageSize);
    Page<Career> searchBySubstring(String substring, int page, int size);
    Career create(String name);
    Career update(String oldName, String newName);
}
