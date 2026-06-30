package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistingCareerValidator.class})
public @interface ExistingCareer {
    String message() default "{validation.career.notExists}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
