package ar.edu.itba.paw.persistence;

public final class JdbcDaoUtils {

    private JdbcDaoUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    // Creates a SQL LIKE pattern by adding wildcards and escaping special characters (%, _).
    public static String likePattern(String text) {
        if (text == null || text.isEmpty()) {
            return "%";
        }
        return "%" + text.replace("%", "\\%").replace("_", "\\_") + "%";
    }

    public static int offset(int page, int size) {
        return (Math.max(1, page) - 1) * size;
    }

    public static int pageCount(int total, int size) {
        return (int) Math.ceil((double) total / size);
    }

}