package ar.edu.itba.paw.webapp.paging;


public final class PaginationConstants {
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 100;
    public static final String PAGE_PARAM_NAME = "page";
    public static final String SIZE_PARAM_NAME = "size";

    private PaginationConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}