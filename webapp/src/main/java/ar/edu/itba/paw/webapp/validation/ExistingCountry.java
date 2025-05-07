package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistingCountryValidator.class})
public @interface ExistingCountry {
    String message() default "{EmailNotInUse.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
