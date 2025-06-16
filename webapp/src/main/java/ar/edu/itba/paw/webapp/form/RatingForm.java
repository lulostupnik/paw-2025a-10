package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidRating;
import javax.validation.constraints.NotNull;

public class RatingForm {
    @ValidRating
    @NotNull
    private Double rating;

    public RatingForm() {
    }

    public RatingForm(Double rating) {
        this.rating = rating;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
