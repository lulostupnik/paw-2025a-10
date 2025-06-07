package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateUserValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUpdateUser {
    String message() default "Username or email already in use";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}