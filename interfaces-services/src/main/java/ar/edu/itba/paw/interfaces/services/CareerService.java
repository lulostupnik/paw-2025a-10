package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.util.Optional;

public interface CareerService {
    Optional<Career> findCareerById(long id);
    Optional<Career> findCareerByName(String name);
    Page<Career> searchCareers(String search, PageParams pageParams);
    Career createCareer(String name);
    void updateCareer(long id, String name);
    void deleteCareer(long id);
}
