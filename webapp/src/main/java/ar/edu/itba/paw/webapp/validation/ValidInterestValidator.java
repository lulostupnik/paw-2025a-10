package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.InterestService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidInterestValidator implements ConstraintValidator<ValidInterest, List<String>> {

    private final InterestService interestService;
    @Autowired
    public ValidInterestValidator(InterestService interestService) {
        this.interestService = interestService;
    }

    @Override
    public void initialize(ValidInterest constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> interests, ConstraintValidatorContext context) {
        if (interests == null || interests.isEmpty()) {
            return true;
        }
        try {
            for(String interest : interests) {
                if (interestService.findInterestByName(interest).isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
