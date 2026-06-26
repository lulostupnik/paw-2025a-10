package ar.edu.itba.paw.models.exceptions;

/**
 * Exception thrown when a referenced entity doesn't exist during create/update operations.
 * For example, creating a university with a non-existent city.
 */
public class InvalidReferenceException extends CustomRuntimeException {

    public InvalidReferenceException(String entityType, String reference) {
        super("exception.InvalidReferenceException", CustomRuntimeException.BAD_REQUEST);
    }

    public InvalidReferenceException(String entityType, long id) {
        super("exception.InvalidReferenceException", CustomRuntimeException.BAD_REQUEST);
    }

    public InvalidReferenceException(String message) {
        super("exception.InvalidReferenceException", CustomRuntimeException.BAD_REQUEST);
    }
}
