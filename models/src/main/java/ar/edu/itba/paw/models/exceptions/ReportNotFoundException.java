package ar.edu.itba.paw.models.exceptions;

public class ReportNotFoundException extends CustomRuntimeException {
    public ReportNotFoundException(long id) {
        super("exception.ReportNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
