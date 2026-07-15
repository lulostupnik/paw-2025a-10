package ar.edu.itba.paw.models.exceptions;

public class UserInterestNotFoundException extends BusinessException {

    public UserInterestNotFoundException() {
        super("exception.UserInterestNotFoundException", BusinessException.NOT_FOUND);
    }
}
