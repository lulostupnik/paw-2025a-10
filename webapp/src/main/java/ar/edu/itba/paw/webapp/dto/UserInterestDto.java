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
    private String interestName; // TODO: esta bien que este acá además de en /interests?
    private int score;

    private URI userUrl;
    private URI interestUrl;
    private URI selfUrl;

    public static UserInterestDto fromUserInterest(final UriInfo uriInfo, final UserInterest userInterest) {
        final UserInterestDto dto = new UserInterestDto();
        dto.interestId = userInterest.getInterest().getId();
        dto.interestName = userInterest.getInterest().getName();
        dto.score = userInterest.getScore();
        dto.userUrl = UriUtils.getUserUri(uriInfo, userInterest.getUser().getId());
        dto.interestUrl = UriUtils.getInterestUri(uriInfo, userInterest.getInterest().getId());
        dto.selfUrl = UriUtils.getUserInterestUri(uriInfo, userInterest.getUser().getId(), userInterest.getInterest().getId());
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

    public URI getUserUrl() {
        return userUrl;
    }

    public URI getInterestUrl() {
        return interestUrl;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }
}
