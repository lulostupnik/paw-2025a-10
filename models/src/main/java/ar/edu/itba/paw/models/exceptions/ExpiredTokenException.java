package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.User;

public class ExpiredTokenException extends BusinessException {
    private final String oldToken;
    public ExpiredTokenException(String message, String oldToken) {
        super("exception.ExpiredTokenException", BusinessException.UNAUTHORIZED);
        this.oldToken = oldToken;
    }
    public String getOldToken() {
        return oldToken;
    }
}
