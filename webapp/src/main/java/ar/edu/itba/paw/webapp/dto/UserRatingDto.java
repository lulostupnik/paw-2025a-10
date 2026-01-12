package ar.edu.itba.paw.webapp.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRatingDto {

    private Double attendedEventsRating;
    private Double hostedEventsRating;
    // private int totalRatings;

    public static UserRatingDto fromRatings(final Double attendedEventsRating, final Double hostedEventsRating) {
        final UserRatingDto dto = new UserRatingDto();
        dto.hostedEventsRating = hostedEventsRating;
        dto.attendedEventsRating = attendedEventsRating;
        return dto;
    }

    public Double getAttendedEventsRating() {
        return attendedEventsRating;
    }
    public Double getHostedEventsRating() {
        return hostedEventsRating;
    }

}
