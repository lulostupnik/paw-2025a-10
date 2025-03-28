package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.University;

import java.util.Optional;

public interface UniversityDao {
    Optional<University> findByName(String name);
    Optional<University> findByAbbreviation(String abbreviation);
    // ¿Poner un findByNameOrAbbreviation?
}
