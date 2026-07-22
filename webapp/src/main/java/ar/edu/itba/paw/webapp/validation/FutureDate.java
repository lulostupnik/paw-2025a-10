package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.validation.FutureDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDate {
    String message() default "{validation.date.future}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}