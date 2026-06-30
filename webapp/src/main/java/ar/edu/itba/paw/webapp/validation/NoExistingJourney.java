package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoExistingJourneyValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoExistingJourney {
    String message() default "{validation.journey.alreadyExists}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
