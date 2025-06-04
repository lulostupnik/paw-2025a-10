package ar.edu.itba.paw.models.exceptions;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
    public ReportNotFoundException() {
        super("Report not found");
    }
}
