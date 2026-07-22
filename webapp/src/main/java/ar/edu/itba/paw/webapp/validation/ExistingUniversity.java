package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistingUniversityValidator.class})
public @interface ExistingUniversity {
    String message() default "{validation.university.notExists}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
