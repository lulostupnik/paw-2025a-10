package ar.edu.itba.paw.models.exceptions;

public abstract class BusinessException extends RuntimeException {

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;

    private final String messageKey;
    private final int status;

    protected BusinessException(final String messageKey, final int status) {
        super(messageKey);
        this.messageKey = messageKey;
        this.status = status;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public int getStatus() {
        return status;
    }
}
