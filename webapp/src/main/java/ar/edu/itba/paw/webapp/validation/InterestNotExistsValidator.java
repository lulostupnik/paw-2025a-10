package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InterestNotExistsValidator implements ConstraintValidator<InterestNotExists, String> {

    private final InterestService interestService;
    @Autowired
    public InterestNotExistsValidator(InterestService interestService) {
        this.interestService = interestService;
    }

    @Override
    public void initialize(InterestNotExists constraintAnnotation) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isEmpty()) {
            return true;
        }
        return interestService.findInterestByName(name).isEmpty();
    }
}
