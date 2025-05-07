package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.FilterJourneyForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FilterDateRangeValidator implements ConstraintValidator<ValidFilterDateRange, FilterJourneyForm> {
    @Override
    public void initialize(ValidFilterDateRange constraintAnnotation) {}

    @Override
    public boolean isValid(FilterJourneyForm form, ConstraintValidatorContext context) {

        if (form.getStartDate() == null || form.getEndDate() == null) {
            return true;
        }

        return !form.getEndDate().isBefore(form.getStartDate());
    }
}