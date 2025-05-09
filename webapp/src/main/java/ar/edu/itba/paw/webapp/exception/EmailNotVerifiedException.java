package ar.edu.itba.paw.webapp.exception;

import org.springframework.security.authentication.AccountStatusException;

public class EmailNotVerifiedException extends AccountStatusException {
    public EmailNotVerifiedException(String msg) {
        super(msg);
    }
}
