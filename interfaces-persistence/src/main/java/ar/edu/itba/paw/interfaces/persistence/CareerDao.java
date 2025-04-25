package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.CursorPage;

import java.util.List;
import java.util.Optional;

public interface CareerDao {
    Optional<Career> findById(long id);
    List<Career> findAll();
    Optional<Career> findByName(String name);
}
