package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Career;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CareerService {
    public Optional<Career> findById(long id);
    public List<Career> findAll();
    public Optional<Career> findByName(String name);
}
