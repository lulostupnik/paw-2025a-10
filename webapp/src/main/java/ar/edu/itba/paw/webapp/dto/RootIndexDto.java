package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriInfo;

public class RootIndexDto {

    // Top-level collection URLs
    private String usersUrl;
    private String eventsUrl;
    private String journeysUrl;
    private String recommendedEventsUrl;
    private String recommendedJourneysUrl;
    private String reportsUrl;
    private String interestsUrl;
    private String universitiesUrl;
    private String careersUrl;
    private String citiesUrl;
    private String countriesUrl;

    // By-ID URI templates
    private String userByIdUrl;
    private String eventByIdUrl;
    private String journeyByIdUrl;
    private String reportByIdUrl;
    private String interestByIdUrl;
    private String universityByIdUrl;
    private String careerByIdUrl;
    private String cityByIdUrl;
    private String countryByIdUrl;

    // User sub-resources
    private String userInterestsUrl;
    private String userInterestByIdUrl;
    private String userRatingUrl;
    private String userProfilePictureUrl;

    // Event sub-resources
    private String eventFlyerUrl;
    private String eventResponsesUrl;
    private String eventResponseByIdUrl;
    private String eventAttendancesUrl;
    private String eventAttendanceByIdUrl;
    private String eventRatingsUrl;
    private String eventRatingByIdUrl;
    private String eventStatisticsUrl;

    // Journey sub-resources
    private String journeyTipsUrl;
    private String journeyTipByIdUrl;
    private String journeyResponsesUrl;
    private String journeyResponseByIdUrl;

    public static RootIndexDto fromUriInfo(final UriInfo uriInfo) {
        final RootIndexDto dto = new RootIndexDto();
        final String baseUri = uriInfo.getBaseUri().toString();

        // Top-level collection URLs with query param templates
        dto.usersUrl = baseUri + "users{?attendingEvent,university,career,interest,search,blocked,page,size}";
        dto.eventsUrl = baseUri + "events{?recommendedForUser,top,destination,interest,afterDate,beforeDate,search,sort,direction,page,size,attendedBy,university,minRating,hasCapacity,creatorId}";
        dto.journeysUrl = baseUri + "journeys{?recommendedForUser,city,university,startDate,endDate,interest,upcoming,past,ongoing,destinationCity,excludeUser,search,sort,direction,page,size}";
        dto.recommendedEventsUrl = baseUri + "events{?recommendedForUser,page,size}";
        dto.recommendedJourneysUrl = baseUri + "journeys{?recommendedForUser,page,size}";
        dto.reportsUrl = baseUri + "reports{?search,page,size}";
        dto.interestsUrl = baseUri + "interests{?search,page,size}";
        dto.universitiesUrl = baseUri + "universities{?search,page,size}";
        dto.careersUrl = baseUri + "careers{?search,page,size}";
        dto.citiesUrl = baseUri + "cities{?search,page,size}";
        dto.countriesUrl = baseUri + "countries";

        // By-ID URI templates
        dto.userByIdUrl = baseUri + "users/{id}";
        dto.eventByIdUrl = baseUri + "events/{id}";
        dto.journeyByIdUrl = baseUri + "journeys/{id}";
        dto.reportByIdUrl = baseUri + "reports/{id}";
        dto.interestByIdUrl = baseUri + "interests/{id}";
        dto.universityByIdUrl = baseUri + "universities/{id}";
        dto.careerByIdUrl = baseUri + "careers/{id}";
        dto.cityByIdUrl = baseUri + "cities/{id}";
        dto.countryByIdUrl = baseUri + "countries/{id}";

        // User sub-resources
        dto.userInterestsUrl = baseUri + "users/{id}/interests{?page,size}";
        dto.userInterestByIdUrl = baseUri + "users/{userId}/interests/{interestId}";
        dto.userRatingUrl = baseUri + "users/{id}/rating";
        dto.userProfilePictureUrl = baseUri + "users/{id}/profilePicture";

        // Event sub-resources
        dto.eventFlyerUrl = baseUri + "events/{id}/flyer";
        dto.eventResponsesUrl = baseUri + "events/{id}/responses{?page,size}";
        dto.eventResponseByIdUrl = baseUri + "events/{eventId}/responses/{responseId}";
        dto.eventAttendancesUrl = baseUri + "events/{id}/attendances{?page,size}";
        dto.eventAttendanceByIdUrl = baseUri + "events/{eventId}/attendances/{userId}";
        dto.eventRatingsUrl = baseUri + "events/{id}/ratings{?page,size}";
        dto.eventRatingByIdUrl = baseUri + "events/{eventId}/ratings/{ratingId}";
        dto.eventStatisticsUrl = baseUri + "events/{id}/statistics";

        // Journey sub-resources
        dto.journeyTipsUrl = baseUri + "journeys/{id}/tips{?search,page,size}";
        dto.journeyTipByIdUrl = baseUri + "journeys/{journeyId}/tips/{tipId}";
        dto.journeyResponsesUrl = baseUri + "journeys/{id}/responses{?page,size}";
        dto.journeyResponseByIdUrl = baseUri + "journeys/{journeyId}/responses/{responseId}";

        return dto;
    }

    // Top-level collection getters/setters
    public String getUsersUrl() { return usersUrl; }
    public void setUsersUrl(String usersUrl) { this.usersUrl = usersUrl; }

    public String getEventsUrl() { return eventsUrl; }
    public void setEventsUrl(String eventsUrl) { this.eventsUrl = eventsUrl; }

    public String getJourneysUrl() { return journeysUrl; }
    public void setJourneysUrl(String journeysUrl) { this.journeysUrl = journeysUrl; }

    public String getRecommendedEventsUrl() { return recommendedEventsUrl; }
    public void setRecommendedEventsUrl(String recommendedEventsUrl) { this.recommendedEventsUrl = recommendedEventsUrl; }

    public String getRecommendedJourneysUrl() { return recommendedJourneysUrl; }
    public void setRecommendedJourneysUrl(String recommendedJourneysUrl) { this.recommendedJourneysUrl = recommendedJourneysUrl; }

    public String getReportsUrl() { return reportsUrl; }
    public void setReportsUrl(String reportsUrl) { this.reportsUrl = reportsUrl; }

    public String getInterestsUrl() { return interestsUrl; }
    public void setInterestsUrl(String interestsUrl) { this.interestsUrl = interestsUrl; }

    public String getUniversitiesUrl() { return universitiesUrl; }
    public void setUniversitiesUrl(String universitiesUrl) { this.universitiesUrl = universitiesUrl; }

    public String getCareersUrl() { return careersUrl; }
    public void setCareersUrl(String careersUrl) { this.careersUrl = careersUrl; }

    public String getCitiesUrl() { return citiesUrl; }
    public void setCitiesUrl(String citiesUrl) { this.citiesUrl = citiesUrl; }

    public String getCountriesUrl() { return countriesUrl; }
    public void setCountriesUrl(String countriesUrl) { this.countriesUrl = countriesUrl; }

    // By-ID URI template getters/setters
    public String getUserByIdUrl() { return userByIdUrl; }
    public void setUserByIdUrl(String userByIdUrl) { this.userByIdUrl = userByIdUrl; }

    public String getEventByIdUrl() { return eventByIdUrl; }
    public void setEventByIdUrl(String eventByIdUrl) { this.eventByIdUrl = eventByIdUrl; }

    public String getJourneyByIdUrl() { return journeyByIdUrl; }
    public void setJourneyByIdUrl(String journeyByIdUrl) { this.journeyByIdUrl = journeyByIdUrl; }

    public String getReportByIdUrl() { return reportByIdUrl; }
    public void setReportByIdUrl(String reportByIdUrl) { this.reportByIdUrl = reportByIdUrl; }

    public String getInterestByIdUrl() { return interestByIdUrl; }
    public void setInterestByIdUrl(String interestByIdUrl) { this.interestByIdUrl = interestByIdUrl; }

    public String getUniversityByIdUrl() { return universityByIdUrl; }
    public void setUniversityByIdUrl(String universityByIdUrl) { this.universityByIdUrl = universityByIdUrl; }

    public String getCareerByIdUrl() { return careerByIdUrl; }
    public void setCareerByIdUrl(String careerByIdUrl) { this.careerByIdUrl = careerByIdUrl; }

    public String getCityByIdUrl() { return cityByIdUrl; }
    public void setCityByIdUrl(String cityByIdUrl) { this.cityByIdUrl = cityByIdUrl; }

    public String getCountryByIdUrl() { return countryByIdUrl; }
    public void setCountryByIdUrl(String countryByIdUrl) { this.countryByIdUrl = countryByIdUrl; }

    // User sub-resource getters/setters
    public String getUserInterestsUrl() { return userInterestsUrl; }
    public void setUserInterestsUrl(String userInterestsUrl) { this.userInterestsUrl = userInterestsUrl; }

    public String getUserInterestByIdUrl() { return userInterestByIdUrl; }
    public void setUserInterestByIdUrl(String userInterestByIdUrl) { this.userInterestByIdUrl = userInterestByIdUrl; }

    public String getUserRatingUrl() { return userRatingUrl; }
    public void setUserRatingUrl(String userRatingUrl) { this.userRatingUrl = userRatingUrl; }

    public String getUserProfilePictureUrl() { return userProfilePictureUrl; }
    public void setUserProfilePictureUrl(String userProfilePictureUrl) { this.userProfilePictureUrl = userProfilePictureUrl; }

    // Event sub-resource getters/setters
    public String getEventFlyerUrl() { return eventFlyerUrl; }
    public void setEventFlyerUrl(String eventFlyerUrl) { this.eventFlyerUrl = eventFlyerUrl; }

    public String getEventResponsesUrl() { return eventResponsesUrl; }
    public void setEventResponsesUrl(String eventResponsesUrl) { this.eventResponsesUrl = eventResponsesUrl; }

    public String getEventResponseByIdUrl() { return eventResponseByIdUrl; }
    public void setEventResponseByIdUrl(String eventResponseByIdUrl) { this.eventResponseByIdUrl = eventResponseByIdUrl; }

    public String getEventAttendancesUrl() { return eventAttendancesUrl; }
    public void setEventAttendancesUrl(String eventAttendancesUrl) { this.eventAttendancesUrl = eventAttendancesUrl; }

    public String getEventAttendanceByIdUrl() { return eventAttendanceByIdUrl; }
    public void setEventAttendanceByIdUrl(String eventAttendanceByIdUrl) { this.eventAttendanceByIdUrl = eventAttendanceByIdUrl; }

    public String getEventRatingsUrl() { return eventRatingsUrl; }
    public void setEventRatingsUrl(String eventRatingsUrl) { this.eventRatingsUrl = eventRatingsUrl; }

    public String getEventRatingByIdUrl() { return eventRatingByIdUrl; }
    public void setEventRatingByIdUrl(String eventRatingByIdUrl) { this.eventRatingByIdUrl = eventRatingByIdUrl; }

    public String getEventStatisticsUrl() { return eventStatisticsUrl; }
    public void setEventStatisticsUrl(String eventStatisticsUrl) { this.eventStatisticsUrl = eventStatisticsUrl; }

    // Journey sub-resource getters/setters
    public String getJourneyTipsUrl() { return journeyTipsUrl; }
    public void setJourneyTipsUrl(String journeyTipsUrl) { this.journeyTipsUrl = journeyTipsUrl; }

    public String getJourneyTipByIdUrl() { return journeyTipByIdUrl; }
    public void setJourneyTipByIdUrl(String journeyTipByIdUrl) { this.journeyTipByIdUrl = journeyTipByIdUrl; }

    public String getJourneyResponsesUrl() { return journeyResponsesUrl; }
    public void setJourneyResponsesUrl(String journeyResponsesUrl) { this.journeyResponsesUrl = journeyResponsesUrl; }

    public String getJourneyResponseByIdUrl() { return journeyResponseByIdUrl; }
    public void setJourneyResponseByIdUrl(String journeyResponseByIdUrl) { this.journeyResponseByIdUrl = journeyResponseByIdUrl; }
}
