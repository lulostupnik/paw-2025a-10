package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInterestDto {

    private long interestId;
    private String interestName;
    private int score;

    private Links links;

    public static UserInterestDto fromUserInterest(final UriInfo uriInfo, final UserInterest userInterest) {
        final UserInterestDto dto = new UserInterestDto();
        dto.interestId = userInterest.getInterest().getId();
        dto.interestName = userInterest.getInterest().getName();
        dto.score = userInterest.getScore();
        final Links links = new Links();
        links.userUrl = UriUtils.getUserUri(uriInfo, userInterest.getUser().getId());
        links.interestUrl = UriUtils.getInterestUri(uriInfo, userInterest.getInterest().getId());
        links.selfUrl = UriUtils.getUserInterestUri(uriInfo, userInterest.getUser().getId(), userInterest.getInterest().getId());
        dto.links = links;
        return dto;
    }

    public static List<UserInterestDto> fromUserInterestCollection(final UriInfo uriInfo, final Collection<UserInterest> userInterests) {
        return userInterests.stream().map(ui -> fromUserInterest(uriInfo, ui)).toList();
    }

    public long getInterestId() {
        return interestId;
    }

    public String getInterestName() {
        return interestName;
    }

    public int getScore() {
        return score;
    }

    public Links getLinks() { return links; }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Links {
        private URI userUrl;
        private URI interestUrl;
        private URI selfUrl;

        public URI getUserUrl() { return userUrl; }
        public URI getInterestUrl() { return interestUrl; }
        public URI getSelfUrl() { return selfUrl; }
    }
}
