package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.User;

public class ExpiredTokenException extends CustomRuntimeException {
    private final String oldToken;
    public ExpiredTokenException(String message, String oldToken) {
        super("exception.ExpiredTokenException", CustomRuntimeException.UNAUTHORIZED);
        this.oldToken = oldToken;
    }
    public String getOldToken() {
        return oldToken;
    }
}
