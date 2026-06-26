package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.links.UserLinks;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPrivateDto {

    private long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private boolean isAdmin;
    private boolean verified;
    private boolean blocked;
    private UserLinks links;

    public static UserPrivateDto fromUser(final UriInfo uriInfo, final User user) {
        final UserPrivateDto dto = new UserPrivateDto();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.firstname = user.getFirstname();
        dto.lastname = user.getLastname();
        dto.email = user.getEmail();
        dto.isAdmin = user.getRole() == ar.edu.itba.paw.models.enums.UserRoles.ADMIN;
        dto.verified = user.isValidated();
        dto.blocked = user.isBlocked();
        dto.links = UserLinks.fromUser(uriInfo, user);
        return dto;
    }

    public static List<UserPrivateDto> fromUserCollection(final UriInfo uriInfo, final Collection<User> users) {
        return users.stream().map(user -> fromUser(uriInfo, user)).toList();
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public boolean isAdmin() { return isAdmin; }
    public boolean isVerified() { return verified; }
    public boolean isBlocked() { return blocked; }
    public UserLinks getLinks() { return links; }
}
