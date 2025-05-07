package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;

public class AttendeesLimitValidator implements ConstraintValidator<ValidAttendeesLimit, Integer> {
    private long max;
    private long min;
    @Override
    public void initialize(ValidAttendeesLimit constraintAnnotation) {
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();

    }

    @Override
    public boolean isValid(Integer attendees, javax.validation.ConstraintValidatorContext context) {
        if (attendees != null) {
            return attendees <= this.max && attendees >= this.min;
        } 
        return true;
    }
}
