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

    // FIXME: Elegir una de las dos funciones y eliminar la otra
    public static String likePattern2(String search) {
        if (search == null || search.isEmpty()) {
            return "%";
        }

        StringBuilder sb = new StringBuilder(search.length() + 4);
        sb.append('%');

        for (int i = 0; i < search.length(); i++) {
            char c = search.charAt(i);
            if (c == '%' || c == '_') {
                sb.append('\\');
            }
            sb.append(c);
        }

        sb.append('%');
        return sb.toString();
    }

    public static int offset(int page, int size) {
        return (Math.max(1, page) - 1) * size;
    }

    public static int pageCount(int total, int size) {
        return (int) Math.ceil((double) total / size);
    }

}