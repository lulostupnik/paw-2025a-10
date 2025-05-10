package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailInUseValidator.class)
public @interface EmailInUse {
    String message() default "Email must be registered in the system";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
