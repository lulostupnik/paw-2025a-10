package ar.edu.itba.paw.webapp.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRatingDto {

    private Double rating;
    // private int totalRatings;

    public static UserRatingDto fromRating(final Double rating) {
        final UserRatingDto dto = new UserRatingDto();
        dto.rating = rating;
        return dto;
    }

    public Double getRating() { return rating; }
}
