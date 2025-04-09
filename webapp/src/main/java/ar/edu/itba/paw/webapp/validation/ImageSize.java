package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageSizeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageSize {
    String message() default "{validation.image.size.message}";
    Class<?>[] groups() default {};
    Class<? extends javax.validation.Payload>[] payload() default {};

    long max() default 2 * 1024 * 1024; // 2MB

}
