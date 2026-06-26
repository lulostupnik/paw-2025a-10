package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserBlockedDto {

    private boolean blocked;
    private Links links;

    public static UserBlockedDto fromUser(final UriInfo uriInfo, final User user) {
        final UserBlockedDto dto = new UserBlockedDto();
        dto.blocked = user.isBlocked();
        final Links links = new Links();
        links.selfUrl = UriUtils.getUserBlockedUri(uriInfo, user.getId());
        links.userUrl = UriUtils.getUserUri(uriInfo, user.getId());
        dto.links = links;
        return dto;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public Links getLinks() {
        return links;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI selfUrl;
        private URI userUrl;

        public URI getSelfUrl() { return selfUrl; }
        public URI getUserUrl() { return userUrl; }
    }
}
