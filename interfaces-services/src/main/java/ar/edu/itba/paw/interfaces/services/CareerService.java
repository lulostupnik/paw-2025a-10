package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.CursorPage;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CareerService {
    Optional<Career> findById(long id);
    List<Career> findAll();
    Optional<Career> findByName(String name);

}
