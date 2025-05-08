package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Class-level annotation for role-based message validation.
 * Allows validator to access both the id and message fields.
 */
@Documented
@Constraint(validatedBy = RoleBasedMessageValidator.class)
@Target({ElementType.TYPE}) // Apply to class level instead of field
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleBasedMessage {
    String message() default "Message is required for admin users";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}