package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, CreateJourneyForm> {
    @Override
    public void initialize(ValidDateRange constraintAnnotation) {}

    @Override
    public boolean isValid(CreateJourneyForm form, ConstraintValidatorContext context) {
        if (form.getStartDate() == null || form.getEndDate() == null) {
            return true; // @NotNull should handle nulls
        }

        if (!form.getEndDate().isAfter(form.getStartDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}