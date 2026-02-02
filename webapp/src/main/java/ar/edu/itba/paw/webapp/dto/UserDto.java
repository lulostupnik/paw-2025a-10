package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDto {

    private  long id;
    private  String username;
    private String email;
    private String role;
    private Boolean isActive; // TODO: ¿Esto está bien tenerlo acá? ¿Es necesario?
    private Links links;
    private String firstname;
    private String lastname;


    public static UserDto fromUser(final UriInfo uriInfo, final User user) {
        final UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.email = user.getEmail();
        dto.username = user.getUsername();
        dto.firstname = user.getFirstname();
        dto.lastname = user.getLastname();
        dto.role = user.getRole().toString().replaceAll("^ROLE_", "").toLowerCase();
        dto.isActive = ! user.isBlocked() && user.isValidated();
        final Links links = new Links();
        links.profilePictureUrl = user.getProfilePictureId() != null
                ? UriUtils.getUserProfilePictureUri(uriInfo, user.getId())
                : null;
        // dto.preferredLanguage = user.getPreferredLanguage();
        links.careerUrl = UriUtils.getCareerUri(uriInfo, user.getCareer().getId());
        links.universityUrl = UriUtils.getUniversityUri(uriInfo, user.getUniversity().getId());
        if(user.getJourney() != null)
            links.journeyUrl = UriUtils.getJourneyUri(uriInfo, user.getJourney().getId());
        links.selfUrl = UriUtils.getUserUri(uriInfo, user.getId());
        dto.links = links;

        return dto;
    }
    public static List<UserDto> fromUserCollection(final UriInfo uriInfo, final java.util.Collection<User> users) {
        return users.stream().map(user -> fromUser(uriInfo, user)).toList();
    }

    public Links getLinks() {
        return links;
    }
    public Boolean getActive() {
        return isActive;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }
    
    public String getLastname() {
        return lastname;
    }

    public long getId() {
        return id;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI careerUrl;
        private URI universityUrl;
        private URI journeyUrl;
        private URI profilePictureUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getCareerUrl() { return careerUrl; }
        public URI getUniversityUrl() { return universityUrl; }
        public URI getJourneyUrl() { return journeyUrl; }
        public URI getProfilePictureUrl() { return profilePictureUrl; }
    }
}
