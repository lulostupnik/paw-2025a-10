package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveException extends CustomRuntimeException {

    public MutuallyExclusiveException(String param1, String param2) {
        super("exception.MutuallyExclusiveException", CustomRuntimeException.BAD_REQUEST);
    }
}
