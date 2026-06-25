package ar.edu.itba.paw.webapp;

public final class CustomMediaType {

    private CustomMediaType() {
        throw new AssertionError();
    }

    // --- USERS ---
    public static final String APPLICATION_USER = "application/vnd.gotogether.user.v1+json";
    public static final String APPLICATION_USER_PRIVATE = "application/vnd.gotogether.user-private.v1+json";
    public static final String APPLICATION_USER_PRIVATE_LIST = "application/vnd.gotogether.user-private-list.v1+json";
    public static final String APPLICATION_USER_PASSWORD = "application/vnd.gotogether.user-password.v1+json";
    public static final String APPLICATION_USER_PASSWORD_RESET = "application/vnd.gotogether.password-reset.v1+json";
    public static final String APPLICATION_USER_VERIFICATION = "application/vnd.gotogether.user-verification.v1+json";
    public static final String APPLICATION_USER_BLOCKED = "application/vnd.gotogether.user-blocked.v1+json";
    public static final String APPLICATION_USER_INTEREST = "application/vnd.gotogether.user-interest.v1+json";
    public static final String APPLICATION_USER_INTEREST_LIST = "application/vnd.gotogether.user-interest-list.v1+json";
    public static final String APPLICATION_USER_RATING = "application/vnd.gotogether.user-rating.v1+json";

    // --- EVENTS ---
    public static final String APPLICATION_EVENT = "application/vnd.gotogether.event.v1+json";
    public static final String APPLICATION_EVENT_LIST = "application/vnd.gotogether.event-list.v1+json";
    public static final String APPLICATION_EVENT_STATISTICS = "application/vnd.gotogether.event-statistics.v1+json";

    // --- EVENT RESPONSES ---
    public static final String APPLICATION_EVENT_RESPONSE = "application/vnd.gotogether.event-response.v1+json";
    public static final String APPLICATION_EVENT_RESPONSE_LIST = "application/vnd.gotogether.event-response-list.v1+json";

    // --- EVENT ATTENDANCES ---
    public static final String APPLICATION_EVENT_ATTENDANCE = "application/vnd.gotogether.event-attendance.v1+json";
    public static final String APPLICATION_EVENT_ATTENDANCE_LIST = "application/vnd.gotogether.event-attendance-list.v1+json";

    // --- EVENT RATINGS ---
    public static final String APPLICATION_EVENT_RATING = "application/vnd.gotogether.event-rating.v1+json";
    public static final String APPLICATION_EVENT_RATING_LIST = "application/vnd.gotogether.event-rating-list.v1+json";

    // --- EVENT DELETE ---
    public static final String APPLICATION_EVENT_DELETE = "application/vnd.gotogether.event-delete.v1+json";

    // --- JOURNEYS ---
    public static final String APPLICATION_JOURNEY = "application/vnd.gotogether.journey.v1+json";
    public static final String APPLICATION_JOURNEY_LIST = "application/vnd.gotogether.journey-list.v1+json";

    // --- JOURNEY RESPONSES ---
    public static final String APPLICATION_JOURNEY_RESPONSE = "application/vnd.gotogether.journey-response.v1+json";
    public static final String APPLICATION_JOURNEY_RESPONSE_LIST = "application/vnd.gotogether.journey-response-list.v1+json";

    // --- JOURNEY DELETE ---
    public static final String APPLICATION_JOURNEY_DELETE = "application/vnd.gotogether.journey-delete.v1+json";

    // --- TIPS ---
    public static final String APPLICATION_TIP = "application/vnd.gotogether.tip.v1+json";
    public static final String APPLICATION_TIP_LIST = "application/vnd.gotogether.tip-list.v1+json";

    // --- CITIES ---
    public static final String APPLICATION_CITY = "application/vnd.gotogether.city.v1+json";
    public static final String APPLICATION_CITY_LIST = "application/vnd.gotogether.city-list.v1+json";

    // --- UNIVERSITIES ---
    public static final String APPLICATION_UNIVERSITY = "application/vnd.gotogether.university.v1+json";
    public static final String APPLICATION_UNIVERSITY_LIST = "application/vnd.gotogether.university-list.v1+json";

    // --- CAREERS ---
    public static final String APPLICATION_CAREER = "application/vnd.gotogether.career.v1+json";
    public static final String APPLICATION_CAREER_LIST = "application/vnd.gotogether.career-list.v1+json";

    // --- INTERESTS ---
    public static final String APPLICATION_INTEREST = "application/vnd.gotogether.interest.v1+json";
    public static final String APPLICATION_INTEREST_LIST = "application/vnd.gotogether.interest-list.v1+json";

    // --- REPORTS ---
    public static final String APPLICATION_REPORT = "application/vnd.gotogether.report.v1+json";
    public static final String APPLICATION_REPORT_LIST = "application/vnd.gotogether.report-list.v1+json";
}
