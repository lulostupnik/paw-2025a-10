package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.FilterEventForm;
import ar.edu.itba.paw.webapp.form.FilterJourneyForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidEventFilterDateRangeValidator implements ConstraintValidator<ValidEventFilterDateRange, FilterEventForm> {
    @Override
    public void initialize(ValidEventFilterDateRange constraintAnnotation) {}

    @Override
    public boolean isValid(FilterEventForm form, ConstraintValidatorContext context) {

        if (form.getStartDate() == null || form.getEndDate() == null) {
            return true;
        }

        return !form.getEndDate().isBefore(form.getStartDate());
    }
}