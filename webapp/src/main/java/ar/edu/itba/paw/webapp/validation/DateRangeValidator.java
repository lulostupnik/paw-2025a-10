package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.DateRangeForm;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRangeForm> {
    @Override
    public void initialize(ValidDateRange constraintAnnotation) {}

    @Override
    public boolean isValid(DateRangeForm form, ConstraintValidatorContext context) {
        if (form.getStartDate() == null || form.getEndDate() == null) {
            return true;
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