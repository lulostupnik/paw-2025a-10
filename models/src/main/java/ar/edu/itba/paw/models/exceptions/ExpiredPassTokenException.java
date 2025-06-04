package ar.edu.itba.paw.models.exceptions;

public class ExpiredPassTokenException extends RuntimeException {
    private String oldToken;
    public ExpiredPassTokenException(String message, String oldToken) {
        super(message);
        this.oldToken = oldToken;
    }
    public String getOldToken() {
        return oldToken;
    }
    public void setOldToken(String oldToken) {
        this.oldToken = oldToken;
    }
}
