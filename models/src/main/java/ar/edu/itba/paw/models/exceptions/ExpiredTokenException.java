package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.User;

public class ExpiredTokenException extends RuntimeException {
    private String oldToken;
    public ExpiredTokenException(String message, String oldToken) {
        super(message);
        this.oldToken = oldToken;
    }
    public String getOldToken() {
        return oldToken;
    }
}
