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
    private URI blockedUrl;

    public static UserLinks fromUser(final UriInfo uriInfo, final User user) {
        final UserLinks links = new UserLinks();
        links.selfUrl = UriUtils.getUserUri(uriInfo, user.getId());
        links.careerUrl = UriUtils.getCareerUri(uriInfo, user.getCareer().getId());
        links.universityUrl = UriUtils.getUniversityUri(uriInfo, user.getUniversity().getId());
        links.profilePictureUrl = user.getProfilePictureId() != null
                ? UriUtils.getUserProfilePictureUri(uriInfo, user.getId())
                : null;
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
    public URI getBlockedUrl() { return blockedUrl; }
}
