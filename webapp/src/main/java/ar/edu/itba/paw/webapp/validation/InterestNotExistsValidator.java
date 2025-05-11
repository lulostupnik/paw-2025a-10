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
        // No initialization needed
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return interestService.findByName(email).isEmpty();
    }
}
