package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.util.Optional;

public interface CareerService {
    Optional<Career> findById(long id);
    Optional<Career> findByName(String name);
    Page<Career> getAllCareers(String search, PageParams pageParams);
    Career create(String name);
    void update(long id, String name);
    void delete(long id);
}
