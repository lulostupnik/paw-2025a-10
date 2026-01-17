package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveFiltersException extends BadRequestException {
    public MutuallyExclusiveFiltersException(String... filterNames) {
        super("The following filters are mutually exclusive and cannot be used together: " + String.join(", ", filterNames));
    }
}
