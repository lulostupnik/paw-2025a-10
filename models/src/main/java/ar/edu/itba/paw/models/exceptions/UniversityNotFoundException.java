package ar.edu.itba.paw.models.exceptions;

public class UniversityNotFoundException extends CustomRuntimeException {

    public UniversityNotFoundException(long id) {
        super("exception.UniversityNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public UniversityNotFoundException(String universityName) {
        super("exception.UniversityNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
