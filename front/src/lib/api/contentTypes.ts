export const ContentTypes = {
    // --- USERS ---
    USER: "application/vnd.gotogether.user.v1+json",
    USER_PRIVATE: "application/vnd.gotogether.user-private.v1+json",
    USER_PRIVATE_LIST: "application/vnd.gotogether.user-private-list.v1+json",
    USER_PASSWORD: "application/vnd.gotogether.user-password.v1+json",
    PASSWORD_RESET: "application/vnd.gotogether.password-reset.v1+json",
    USER_VERIFICATION: "application/vnd.gotogether.user-verification.v1+json",
    USER_BLOCKED: "application/vnd.gotogether.user-blocked.v1+json",
    USER_INTEREST: "application/vnd.gotogether.user-interest.v1+json",
    USER_INTEREST_LIST: "application/vnd.gotogether.user-interest-list.v1+json",
    USER_RATING: "application/vnd.gotogether.user-rating.v1+json",

    // --- EVENTS ---
    EVENT: "application/vnd.gotogether.event.v1+json",
    EVENT_LIST: "application/vnd.gotogether.event-list.v1+json",
    EVENT_STATISTICS: "application/vnd.gotogether.event-statistics.v1+json",

    // --- EVENT RESPONSES ---
    EVENT_RESPONSE: "application/vnd.gotogether.event-response.v1+json",
    EVENT_RESPONSE_LIST: "application/vnd.gotogether.event-response-list.v1+json",

    // --- EVENT ATTENDANCES ---
    EVENT_ATTENDANCE: "application/vnd.gotogether.event-attendance.v1+json",
    EVENT_ATTENDANCE_LIST: "application/vnd.gotogether.event-attendance-list.v1+json",

    // --- EVENT RATINGS ---
    EVENT_RATING: "application/vnd.gotogether.event-rating.v1+json",
    EVENT_RATING_LIST: "application/vnd.gotogether.event-rating-list.v1+json",

    // --- EVENT DELETE ---
    EVENT_DELETE: "application/vnd.gotogether.event-delete.v1+json",

    // --- JOURNEYS ---
    JOURNEY: "application/vnd.gotogether.journey.v1+json",
    JOURNEY_LIST: "application/vnd.gotogether.journey-list.v1+json",

    // --- JOURNEY RESPONSES ---
    JOURNEY_RESPONSE: "application/vnd.gotogether.journey-response.v1+json",
    JOURNEY_RESPONSE_LIST: "application/vnd.gotogether.journey-response-list.v1+json",

    // --- JOURNEY DELETE ---
    JOURNEY_DELETE: "application/vnd.gotogether.journey-delete.v1+json",

    // --- TIPS ---
    TIP: "application/vnd.gotogether.tip.v1+json",
    TIP_LIST: "application/vnd.gotogether.tip-list.v1+json",

    // --- CITIES ---
    CITY: "application/vnd.gotogether.city.v1+json",
    CITY_LIST: "application/vnd.gotogether.city-list.v1+json",

    // --- UNIVERSITIES ---
    UNIVERSITY: "application/vnd.gotogether.university.v1+json",
    UNIVERSITY_LIST: "application/vnd.gotogether.university-list.v1+json",

    // --- CAREERS ---
    CAREER: "application/vnd.gotogether.career.v1+json",
    CAREER_LIST: "application/vnd.gotogether.career-list.v1+json",

    // --- INTERESTS ---
    INTEREST: "application/vnd.gotogether.interest.v1+json",
    INTEREST_LIST: "application/vnd.gotogether.interest-list.v1+json",

    // --- REPORTS ---
    REPORT: "application/vnd.gotogether.report.v1+json",
    REPORT_LIST: "application/vnd.gotogether.report-list.v1+json",

    // --- STANDARD ---
    JSON: "application/json",
    MULTIPART: "multipart/form-data",
} as const;

export type ContentType = (typeof ContentTypes)[keyof typeof ContentTypes];

export function buildHeaders(
    contentType?: string | null,
    acceptType?: string | null,
): Record<string, string> {
    const headers: Record<string, string> = {
        Accept: acceptType || ContentTypes.JSON,
    };

    if (contentType && contentType !== ContentTypes.MULTIPART) {
        headers["Content-Type"] = contentType;
    }

    return headers;
}
