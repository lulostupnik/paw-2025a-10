package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = RoleBasedMessageValidator.class)
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleBasedMessage {
    String message() default "Message is required for admin users";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}