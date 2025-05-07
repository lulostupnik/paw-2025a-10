package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.validation.ValidInterestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidInterestValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInterest {
    String message() default "User already has a journey";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
