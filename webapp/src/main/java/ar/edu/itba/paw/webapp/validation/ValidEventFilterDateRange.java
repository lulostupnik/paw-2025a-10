package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidEventFilterDateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventFilterDateRange {
    String message() default "{validation.dateRange.endBeforeStart}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}