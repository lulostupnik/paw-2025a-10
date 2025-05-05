package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import java.util.List;
import java.util.Optional;

public interface CareerService {
    Optional<Career> findById(long id);
    List<Career> findAll();
    Optional<Career> findByName(String name);
    Page<Career> getAllCareers(String search, int page, int pageSize);
    Career create(String name);
    Career update(long id, String name);
    void delete(long id);
    String getCareersJSON(String search);
}
