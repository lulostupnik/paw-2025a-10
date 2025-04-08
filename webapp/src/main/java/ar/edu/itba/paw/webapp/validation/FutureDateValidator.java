package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureDateValidator implements ConstraintValidator<FutureDate, LocalDate> {
    @Override
    public void initialize(FutureDate constraintAnnotation) {}

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // Maneja con @NotNull si es necesario
        }
        return date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now());
    }
}