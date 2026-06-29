package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidInterestValidator implements ConstraintValidator<ValidInterest, List<Long>> {

    private final InterestService interestService;
    @Autowired
    public ValidInterestValidator(InterestService interestService) {
        this.interestService = interestService;
    }

    @Override
    public void initialize(ValidInterest constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<Long> interestIds, ConstraintValidatorContext context) {
        if (interestIds == null || interestIds.isEmpty()) {
            return true;
        }
        try {
            for(Long interestId : interestIds) {
                if (interestId == null || interestService.findInterestById(interestId).isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
