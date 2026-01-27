package ar.edu.itba.paw.webapp.utils;

import ar.edu.itba.paw.models.exceptions.InvalidDateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    private DateUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Invalid date format: " + dateStr + ". Expected format: " + Constants.DATE_FORMAT);
        }
    }
}
