package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.validation.ContentTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContentTypeValidator.class)
public @interface ContentType {
    String message() default "Invalid file type. Only JPEG, JPG and PNG files are allowed.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] value();
}