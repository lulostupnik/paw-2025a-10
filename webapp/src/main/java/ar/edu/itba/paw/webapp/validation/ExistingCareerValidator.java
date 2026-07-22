package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingCareerValidator implements ConstraintValidator<ExistingCareer, Long> {

    private final CareerService careerService;
    @Autowired
    public ExistingCareerValidator(CareerService careerService) {
        this.careerService = careerService;
    }

    @Override
    public void initialize(ExistingCareer constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long careerId, ConstraintValidatorContext context) {
        if (careerId == null) {
            return true;
        }
        return careerService.findCareerById(careerId).isPresent();
    }
}
