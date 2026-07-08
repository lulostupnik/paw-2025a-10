package ar.edu.itba.paw.models.exceptions;

public class ReportNotFoundException extends BusinessException {
    public ReportNotFoundException(long id) {
        super("exception.ReportNotFoundException", BusinessException.NOT_FOUND);
    }
}
