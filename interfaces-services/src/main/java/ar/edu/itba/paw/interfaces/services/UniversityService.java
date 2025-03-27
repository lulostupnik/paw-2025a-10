package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.University;

import java.util.Optional;

public interface UniversityService {
    Optional<University> findByName(String name);
}
