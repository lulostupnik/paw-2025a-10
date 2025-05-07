package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CareerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingCareerValidator implements ConstraintValidator<ExistingCareer, String> {

    @Autowired
    private CareerService careerService;

    @Override
    public void initialize(ExistingCareer constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String careerName, ConstraintValidatorContext context) {
        if (careerName == null || careerName.isEmpty()) {
            return true;
        }
        return careerService.findByName(careerName).isPresent(); // Placeholder, replace with actual logic
    }
}
