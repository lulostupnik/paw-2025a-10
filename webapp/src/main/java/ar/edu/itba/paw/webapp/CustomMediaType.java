package ar.edu.itba.paw.webapp;

public final class CustomMediaType {

    public static final String USER_PASSWORD = "application/vnd.gotogether.userPassword.v1+json";
    public static final String PASSWORD_RESET = "application/vnd.gotogether.passwordReset.v1+json";
    public static final String ACCOUNT_VALIDATION_TOKEN = "application/vnd.gotogether.accountValidationToken.v1+json";

    private CustomMediaType() {
        throw new AssertionError();
    }
}
