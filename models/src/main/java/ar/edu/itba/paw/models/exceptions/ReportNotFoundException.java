package ar.edu.itba.paw.models.exceptions;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(long id) {
        super(String.format("Report with id %d not found", id));
    }


}
