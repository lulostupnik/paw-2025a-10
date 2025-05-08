package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation for role-based message validation.
 * Message can be null for OWNER role but must be non-null for ADMIN role.
 */
@Documented
@Constraint(validatedBy = RoleBasedMessageValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleBasedMessage {
    String message() default "Message cannot be empty for admin users";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}