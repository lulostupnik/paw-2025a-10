package ar.edu.itba.paw.models.exceptions;

public class UniversityNotFoundException extends RuntimeException {


    public UniversityNotFoundException(long id) {
        super(String.format("University with id %d not found", id));
    }

    public UniversityNotFoundException(String universityName) {
        super(String.format("University with name %s not found", universityName));
    }


}
