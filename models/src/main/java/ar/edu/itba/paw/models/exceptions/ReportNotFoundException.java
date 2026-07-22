package ar.edu.itba.paw.models.exceptions;

public class ReportNotFoundException extends BusinessException {

    public ReportNotFoundException() {
        super("exception.ReportNotFoundException", BusinessException.NOT_FOUND);
    }
}
