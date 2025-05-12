package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CareerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CareerNotExistsValidator implements ConstraintValidator<CareerNotExists, String> {

    private final CareerService careerService;

    @Autowired
    public CareerNotExistsValidator(CareerService careerService) {
        this.careerService = careerService;
    }

    @Override
    public void initialize(CareerNotExists constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return careerService.findByName(value).isEmpty();
    }

}
