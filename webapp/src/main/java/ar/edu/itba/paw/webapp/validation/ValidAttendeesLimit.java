package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.validation.AttendeesLimitValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AttendeesLimitValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAttendeesLimit {
    String message() default "{validation.attendeesLimit.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long max() default 1000000;
    long min() default 2;
}