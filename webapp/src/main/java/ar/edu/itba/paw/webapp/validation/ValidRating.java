package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidRatingValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRating {
    String message() default "{validation.rating.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}