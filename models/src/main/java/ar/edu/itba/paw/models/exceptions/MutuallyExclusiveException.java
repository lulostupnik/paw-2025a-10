package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveException extends BusinessException {

    public MutuallyExclusiveException(String param1, String param2) {
        super("exception.MutuallyExclusiveException", BusinessException.BAD_REQUEST);
    }
}
