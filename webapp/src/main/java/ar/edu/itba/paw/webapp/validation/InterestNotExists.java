package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.validation.InterestNotExistsValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {InterestNotExistsValidator.class})
public @interface InterestNotExists {
    String message() default "{validation.interest.not.exists.message}";
    Class<?>[] groups() default {};
    Class<? extends javax.validation.Payload>[] payload() default {};
}
