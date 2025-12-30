package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public final class UriUtils {
    public static final String API_BASE_URL = "/api";
    public static final String USERS_URL = API_BASE_URL + "/users";
    public static final String JOURNEYS_URL = API_BASE_URL + "/journeys";
    public static final String EVENTS_URL = API_BASE_URL + "/events";
    public static final String REPORTS_URL = API_BASE_URL + "/reports";
    public static final String INTERESTS_URL = API_BASE_URL + "/interests";
    public static final String UNIVERSITIES_URL = API_BASE_URL + "/universities";
    public static final String CAREERS_URL = API_BASE_URL + "/careers";
    public static final String CITIES_URL = API_BASE_URL + "/cities";
    public static final String COUNTRIES_URL = API_BASE_URL + "/countries";
    public static final String IMAGES_URL = API_BASE_URL + "/images";
    public static final String PASSWORD_RESET_TOKENS_URL = API_BASE_URL + "/password-reset-tokens";
    public static final String RATINGS_URL = API_BASE_URL + "/ratings";

    private UriUtils() {
        // Utility class
    }


    public static URI getUsersUri(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(USERS_URL).build();
    }

    public static URI getUserUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path(USERS_URL).path(String.valueOf(userId)).build();
    }

    public static URI getUserInterestsUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path(USERS_URL).path(String.valueOf(userId)).path("interests").build();
    }

    public static URI getUserInterestUri(final UriInfo uriInfo, final long userId, final long interestId) {
        return uriInfo.getBaseUriBuilder().path(USERS_URL).path(String.valueOf(userId))
                .path("interests").path(String.valueOf(interestId)).build();
    }

    public static URI getUserRatingUri(final UriInfo uriInfo, final long userId) {
        return uriInfo.getBaseUriBuilder().path(USERS_URL).path(String.valueOf(userId)).path("rating").build();
    }

    // ==================== JOURNEYS ====================

    public static URI getJourneysUri(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(JOURNEYS_URL).build();
    }

    public static URI getJourneyUri(final UriInfo uriInfo, final long journeyId) {
        return uriInfo.getBaseUriBuilder().path(JOURNEYS_URL).path(String.valueOf(journeyId)).build();
    }

    // Tips (sub-resource of journeys)
    public static URI getJourneyTipsUri(final UriInfo uriInfo, final long journeyId) {
        return uriInfo.getBaseUriBuilder().path(JOURNEYS_URL).path(String.valueOf(journeyId)).path("tips").build();
    }

    public static URI getJourneyTipUri(final UriInfo uriInfo, final long journeyId, final long tipId) {
        return uriInfo.getBaseUriBuilder().path(JOURNEYS_URL).path(String.valueOf(journeyId))
                .path("tips").path(String.valueOf(tipId)).build();
    }

    // Journey Responses (sub-resource of journeys)
    public static URI getJourneyResponsesUri(final UriInfo uriInfo, final long journeyId) {
        return uriInfo.getBaseUriBuilder().path(JOURNEYS_URL).path(String.valueOf(journeyId)).path("responses").build();
    }

    public static URI getJourneyResponseUri(final UriInfo uriInfo, final long journeyId, final long responseId) {
        return uriInfo.getBaseUriBuilder().path(JOURNEYS_URL).path(String.valueOf(journeyId))
                .path("responses").path(String.valueOf(responseId)).build();
    }

    // ==================== EVENTS ====================

    public static URI getEventsUri(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(EVENTS_URL).build();
    }

    public static URI getEventUri(final UriInfo uriInfo, final long eventId) {
        return uriInfo.getBaseUriBuilder().path(EVENTS_URL).path(String.valueOf(eventId)).build();
    }

    // Event Responses (sub-resource of events)
    public static URI getEventResponsesUri(final UriInfo uriInfo, final long eventId) {
        return uriInfo.getBaseUriBuilder().path(EVENTS_URL).path(String.valueOf(eventId)).path("responses").build();
    }

    public static URI getEventResponseUri(final UriInfo uriInfo, final long eventId, final long responseId) {
        return uriInfo.getBaseUriBuilder().path(EVENTS_URL).path(String.valueOf(eventId))
                .path("responses").path(String.valueOf(responseId)).build();
    }

    // Attendances (sub-resource of events)
    public static URI getEventAttendancesUri(final UriInfo uriInfo, final long eventId) {
        return uriInfo.getBaseUriBuilder().path(EVENTS_URL).path(String.valueOf(eventId)).path("attendances").build();
    }

    // Ratings (sub-resource of events)
    public static URI getEventRatingsUri(final UriInfo uriInfo, final long eventId) {
        return uriInfo.getBaseUriBuilder().path(EVENTS_URL).path(String.valueOf(eventId)).path("ratings").build();
    }

    public static URI getEventRatingUri(final UriInfo uriInfo, final long eventId, final long ratingId) {
        return uriInfo.getBaseUriBuilder().path(EVENTS_URL).path(String.valueOf(eventId))
                .path("ratings").path(String.valueOf(ratingId)).build();
    }

    // ==================== REPORTS ====================

    public static URI getReportsUri(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(REPORTS_URL).build();
    }

    public static URI getReportUri(final UriInfo uriInfo, final long reportId) {
        return uriInfo.getBaseUriBuilder().path(REPORTS_URL).path(String.valueOf(reportId)).build();
    }

    // ==================== INTERESTS ====================

    public static URI getInterestsUri(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(INTERESTS_URL).build();
    }

    public static URI getInterestUri(final UriInfo uriInfo, final long interestId) {
        return uriInfo.getBaseUriBuilder().path(INTERESTS_URL).path(String.valueOf(interestId)).build();
    }

    // ==================== UNIVERSITIES ====================

    public static URI getUniversitiesUri(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(UNIVERSITIES_URL).build();
    }

}
