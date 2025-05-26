package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;

public class ValidRatingValidator implements ConstraintValidator<ValidRating,Double> {

    @Override
    public void initialize(ValidRating constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Double rating, javax.validation.ConstraintValidatorContext context) {
        if (rating == null) {
            return true; // Null values are considered valid
        }
        return rating >= 0 && rating <= 5 && (rating * 10) % 5 == 0;
    }

}
