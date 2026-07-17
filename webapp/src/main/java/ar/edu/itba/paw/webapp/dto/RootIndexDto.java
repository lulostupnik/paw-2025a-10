package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RootIndexDto {

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

    private String userByIdUrl;
    private String eventByIdUrl;
    private String journeyByIdUrl;
    private String reportByIdUrl;
    private String interestByIdUrl;
    private String universityByIdUrl;
    private String careerByIdUrl;
    private String cityByIdUrl;
    private String countryByIdUrl;

    private String userInterestsUrl;
    private String userInterestByIdUrl;
    private String userRatingUrl;
    private String userProfilePictureUrl;

    private String eventFlyerUrl;
    private String eventResponsesUrl;
    private String eventResponseByIdUrl;
    private String eventAttendancesUrl;
    private String eventAttendanceByIdUrl;
    private String eventRatingsUrl;
    private String eventRatingByIdUrl;
    private String eventStatisticsUrl;

    private String journeyTipsUrl;
    private String journeyTipByIdUrl;
    private String journeyResponsesUrl;
    private String journeyResponseByIdUrl;

    public static RootIndexDto fromUriInfo(final UriInfo uriInfo) {
        final RootIndexDto dto = new RootIndexDto();
        final String baseUri = uriInfo.getBaseUri().toString();

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

        dto.userByIdUrl = baseUri + "users/{id}";
        dto.eventByIdUrl = baseUri + "events/{id}";
        dto.journeyByIdUrl = baseUri + "journeys/{id}";
        dto.reportByIdUrl = baseUri + "reports/{id}";
        dto.interestByIdUrl = baseUri + "interests/{id}";
        dto.universityByIdUrl = baseUri + "universities/{id}";
        dto.careerByIdUrl = baseUri + "careers/{id}";
        dto.cityByIdUrl = baseUri + "cities/{id}";
        dto.countryByIdUrl = baseUri + "countries/{id}";

        dto.userInterestsUrl = baseUri + "users/{id}/interests{?page,size}";
        dto.userInterestByIdUrl = baseUri + "users/{userId}/interests/{interestId}";
        dto.userRatingUrl = baseUri + "users/{id}/rating";
        dto.userProfilePictureUrl = baseUri + "users/{id}/profilePicture";

        dto.eventFlyerUrl = baseUri + "events/{id}/flyer";
        dto.eventResponsesUrl = baseUri + "events/{id}/responses{?page,size}";
        dto.eventResponseByIdUrl = baseUri + "events/{eventId}/responses/{responseId}";
        dto.eventAttendancesUrl = baseUri + "events/{id}/attendances{?page,size}";
        dto.eventAttendanceByIdUrl = baseUri + "events/{eventId}/attendances/{userId}";
        dto.eventRatingsUrl = baseUri + "events/{id}/ratings{?page,size}";
        dto.eventRatingByIdUrl = baseUri + "events/{eventId}/ratings/{ratingId}";
        dto.eventStatisticsUrl = baseUri + "events/{id}/statistics";

        dto.journeyTipsUrl = baseUri + "journeys/{id}/tips{?search,page,size}";
        dto.journeyTipByIdUrl = baseUri + "journeys/{journeyId}/tips/{tipId}";
        dto.journeyResponsesUrl = baseUri + "journeys/{id}/responses{?page,size}";
        dto.journeyResponseByIdUrl = baseUri + "journeys/{journeyId}/responses/{responseId}";

        return dto;
    }

    public String getUsersUrl() { return usersUrl; }

    public String getEventsUrl() { return eventsUrl; }

    public String getJourneysUrl() { return journeysUrl; }

    public String getRecommendedEventsUrl() { return recommendedEventsUrl; }

    public String getRecommendedJourneysUrl() { return recommendedJourneysUrl; }

    public String getReportsUrl() { return reportsUrl; }

    public String getInterestsUrl() { return interestsUrl; }

    public String getUniversitiesUrl() { return universitiesUrl; }

    public String getCareersUrl() { return careersUrl; }

    public String getCitiesUrl() { return citiesUrl; }

    public String getCountriesUrl() { return countriesUrl; }

    public String getUserByIdUrl() { return userByIdUrl; }

    public String getEventByIdUrl() { return eventByIdUrl; }

    public String getJourneyByIdUrl() { return journeyByIdUrl; }

    public String getReportByIdUrl() { return reportByIdUrl; }

    public String getInterestByIdUrl() { return interestByIdUrl; }

    public String getUniversityByIdUrl() { return universityByIdUrl; }

    public String getCareerByIdUrl() { return careerByIdUrl; }

    public String getCityByIdUrl() { return cityByIdUrl; }

    public String getCountryByIdUrl() { return countryByIdUrl; }

    public String getUserInterestsUrl() { return userInterestsUrl; }

    public String getUserInterestByIdUrl() { return userInterestByIdUrl; }

    public String getUserRatingUrl() { return userRatingUrl; }

    public String getUserProfilePictureUrl() { return userProfilePictureUrl; }

    public String getEventFlyerUrl() { return eventFlyerUrl; }

    public String getEventResponsesUrl() { return eventResponsesUrl; }

    public String getEventResponseByIdUrl() { return eventResponseByIdUrl; }

    public String getEventAttendancesUrl() { return eventAttendancesUrl; }

    public String getEventAttendanceByIdUrl() { return eventAttendanceByIdUrl; }

    public String getEventRatingsUrl() { return eventRatingsUrl; }

    public String getEventRatingByIdUrl() { return eventRatingByIdUrl; }

    public String getEventStatisticsUrl() { return eventStatisticsUrl; }

    public String getJourneyTipsUrl() { return journeyTipsUrl; }

    public String getJourneyTipByIdUrl() { return journeyTipByIdUrl; }

    public String getJourneyResponsesUrl() { return journeyResponsesUrl; }

    public String getJourneyResponseByIdUrl() { return journeyResponseByIdUrl; }
}
