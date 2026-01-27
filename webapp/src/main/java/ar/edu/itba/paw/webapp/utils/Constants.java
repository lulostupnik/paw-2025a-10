package ar.edu.itba.paw.webapp.utils;

public final class Constants {

    public static final int MAX_IMAGE_SIZE = 50 * 1024 * 1024; // 50 MB

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private Constants() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
