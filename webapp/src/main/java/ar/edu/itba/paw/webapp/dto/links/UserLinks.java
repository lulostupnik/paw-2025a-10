package ar.edu.itba.paw.webapp.dto.links;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.net.URI;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserLinks {

    private URI selfUrl;
    private URI careerUrl;
    private URI universityUrl;
    private URI journeyUrl;
    private URI profilePictureUrl;
    private URI createdEventsUrl;
    private URI attendedEventsUrl;
    private URI recommendedEventsUrl;
    private URI recommendedJourneysUrl;

    public static UserLinks fromUser(final UriInfo uriInfo, final User user) {
        final UserLinks links = new UserLinks();
        links.selfUrl = UriUtils.getUserUri(uriInfo, user.getId());
        links.careerUrl = UriUtils.getCareerUri(uriInfo, user.getCareer().getId());
        links.universityUrl = UriUtils.getUniversityUri(uriInfo, user.getUniversity().getId());
        links.profilePictureUrl = user.getProfilePictureId() != null
                ? UriUtils.getUserProfilePictureUri(uriInfo, user.getId())
                : null;
        links.createdEventsUrl = UriUtils.getUserCreatedEventsUri(uriInfo, user.getId());
        links.attendedEventsUrl = UriUtils.getUserAttendedEventsUri(uriInfo, user.getId());
        links.recommendedEventsUrl = UriUtils.getUserRecommendedEventsUri(uriInfo, user.getId());
        links.recommendedJourneysUrl = UriUtils.getUserRecommendedJourneysUri(uriInfo, user.getId());
        if (user.hasActiveJourney()) {
            links.journeyUrl = UriUtils.getJourneyUri(uriInfo, user.getJourney().getId());
        }
        return links;
    }

    public URI getSelfUrl() { return selfUrl; }
    public URI getCareerUrl() { return careerUrl; }
    public URI getUniversityUrl() { return universityUrl; }
    public URI getJourneyUrl() { return journeyUrl; }
    public URI getProfilePictureUrl() { return profilePictureUrl; }
    public URI getCreatedEventsUrl() { return createdEventsUrl; }
    public URI getAttendedEventsUrl() { return attendedEventsUrl; }
    public URI getRecommendedEventsUrl() { return recommendedEventsUrl; }
    public URI getRecommendedJourneysUrl() { return recommendedJourneysUrl; }
}
