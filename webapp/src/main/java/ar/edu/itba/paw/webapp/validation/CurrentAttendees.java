package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CurrentAttendeeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentAttendees {
    String message() default "Attendees limit cannot be less than current attendees count";
    Class<?>[] groups() default {};
    Class<? extends javax.validation.Payload>[] payload() default {};
}
