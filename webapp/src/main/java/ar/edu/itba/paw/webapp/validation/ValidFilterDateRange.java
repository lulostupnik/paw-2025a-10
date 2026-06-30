package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilterDateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFilterDateRange {
    String message() default "{validation.dateRange.endBeforeStart}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}