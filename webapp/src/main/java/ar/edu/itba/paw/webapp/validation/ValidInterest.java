package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy =  {ValidInterestIdValidator.class, ValidInterestValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInterest {
    String message() default "Not a valid interest selection";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
