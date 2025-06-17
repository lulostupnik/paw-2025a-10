package ar.edu.itba.paw.models.exceptions;

public class UniversityAlreadyExistsException extends RuntimeException {
    public UniversityAlreadyExistsException(String name, String city) {
        super(String.format("University with name %s and city %s already exists", name, city));
    }

}
