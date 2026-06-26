package ar.edu.itba.paw.models.exceptions;

public class UniversityAlreadyExistsException extends CustomRuntimeException {
    public UniversityAlreadyExistsException(String name, String city) {
        super("exception.UniversityAlreadyExistsException", CustomRuntimeException.CONFLICT);
    }

}
