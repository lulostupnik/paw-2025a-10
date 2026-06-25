package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingCareerValidator implements ConstraintValidator<ExistingCareer, String> {

    private final CareerService careerService;
    @Autowired
    public ExistingCareerValidator(CareerService careerService) {
        this.careerService = careerService;
    }

    @Override
    public void initialize(ExistingCareer constraintAnnotation) {
    }

    @Override
    public boolean isValid(String careerName, ConstraintValidatorContext context) {
        if (careerName == null || careerName.isEmpty()) {
            return true;
        }
        return careerService.findCareerByName(careerName).isPresent();
    }
}
