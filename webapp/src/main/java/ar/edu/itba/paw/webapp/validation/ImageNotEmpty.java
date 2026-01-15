package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageNotEmptyValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageNotEmpty {
    String message() default "Image is required";
    Class<?>[] groups() default {};
    Class<? extends javax.validation.Payload>[] payload() default {};
}
