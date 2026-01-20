package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveException extends BadRequestException {

    public MutuallyExclusiveException(String param1, String param2) {
        super(String.format("Parameters '%s' and '%s' are mutually exclusive", param1, param2));
    }
}
