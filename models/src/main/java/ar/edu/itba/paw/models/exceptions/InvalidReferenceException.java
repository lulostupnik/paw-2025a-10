package ar.edu.itba.paw.models.exceptions;

/**
 * Exception thrown when a referenced entity doesn't exist during create/update operations.
 * For example, creating a university with a non-existent city.
 */
public class InvalidReferenceException extends BadRequestException {

    public InvalidReferenceException(String entityType, String reference) {
        super(String.format("%s not found: %s", entityType, reference));
    }

    public InvalidReferenceException(String entityType, long id) {
        super(String.format("%s with id %d not found", entityType, id));
    }

    public InvalidReferenceException(String message) {
        super(message);
    }
}
